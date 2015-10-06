/*
Copyright 2012 Artem Stasuk

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.terma.javaniotcpserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple TCP server based on NIO.
 *
 * Server use workers for process incoming client connections.
 *
 * Worker is thread, it waits on own selector {@link java.nio.channels.Selector}
 *
 * Only one worker processes accept for incoming client connection, after
 * that this worker uses @{link TcpServerHandlerFactory} for create
 * handler @{link TcpServerHandler} and add it to not started handlers queue. All workers
 * have access to this queue.
 *
 * Worker has next lifecycle: try to get one not started handler from queue
 * if it exists register it, then wait on selector with timeout, get IO events
 * for each event get attached handler from key and process it.
 * After that worker returns to step with queue.
 *
 * @see TcpServerConfig
 * @see TcpServerHandler
 * @see TcpServerHandlerFactory
 */
public class TcpServer {

    private final static Logger LOGGER = Logger.getAnonymousLogger();

    private final TcpServerConfig config;
    private final String name;

    private Queue<TcpServerHandler> handlers;
    private Thread[] workers;

    /**
     * @param config - config
     * @throws IllegalArgumentException - when worker count less than 1
     */
    public TcpServer(final TcpServerConfig config) {
        if (config == null)
            throw new NullPointerException("Please specify config! Thx!");

        this.config = config;
        name = "TcpServer on port " + config.getPort();
    }

    /**
     * This method starts waiting incoming connections for proxy to remote host.
     * Method return control when all worker will be started, it isn't block.
     *
     * @throws UnsupportedOperationException - if you try to start already started connector
     */
    public void start() {
        if (workers != null) throw new UnsupportedOperationException("Please shutdown connector!");

        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("Starting " + name + " with " + config.getWorkerCount() + " workers");

        handlers = new ConcurrentLinkedQueue<TcpServerHandler>();
        handlers.add(new TcpServerAcceptor(config, handlers));

        workers = new Thread[config.getWorkerCount()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new TcpServerWorker(handlers);
        }

        for (final Thread worker : workers) worker.start();

        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info(name + " started");
    }

    /**
     * Shutdown connector.
     *
     * This method wait when all resources will be closed.
     * You can call this method any time.
     * No problem and exceptions if you try to shutdown connector twice without start.
     */
    public void shutdown() {
        if (workers == null) {
            if (LOGGER.isLoggable(Level.INFO))
                LOGGER.info(name + " already been shutdown");
            return;
        }

        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("Starting shutdown " + name);

        for (final Thread worker : workers) {
            worker.interrupt();
            try {
                worker.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        workers = null;

        TcpServerHandler handler;
        while ((handler = handlers.poll()) != null) handler.destroy();
        handlers = null;

        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info(name + " was shutdown");
    }

}
