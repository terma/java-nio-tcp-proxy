package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;
import com.github.javaniotcpproxy.handler.TcpProxyHandler;
import com.github.javaniotcpproxy.handler.TcpProxyHandlerAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class TcpProxyConnector {

    private final static Logger LOGGER = Logger.getAnonymousLogger();

    private final List<Thread> workers = new ArrayList<Thread>();
    private final TcpProxyConfig config;
    private final String name;

    public TcpProxyConnector(final TcpProxyConfig config) {
        this.config = config;
        name = "TcpProxyConnector to " + config.getRemoteHost() + ":" + config.getRemotePort()
                + " from " + config.getLocalPort();
    }

    public void start(final int workerCount) throws IOException {
        if (workerCount < 1) throw new IllegalArgumentException("Count of workers should be more 1!");

        LOGGER.info("Starting " + name + " with " + workerCount + " workers");

        final ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new InetSocketAddress(config.getLocalPort()));
        LOGGER.info(name + " listener was started");
        server.configureBlocking(false);

        final Selector[] selectors = new Selector[workerCount];
        for (int i = 0; i < selectors.length; i++) {
            selectors[i] = Selector.open();
        }

        server.register(selectors[0], SelectionKey.OP_ACCEPT,
                new TcpProxyHandlerAcceptor(selectors, config));

        for (final Selector selector : selectors) {
            final Thread worker = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (!Thread.interrupted()) {
                            selector.select();

                            final Set<SelectionKey> keys = selector.selectedKeys();
                            for (final SelectionKey key : keys) {
                                final TcpProxyHandler handler = (TcpProxyHandler) key.attachment();
                                handler.process(key);
                            }
                            keys.clear();
                        }
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }

            });

            workers.add(worker);
            worker.start();
        }

        LOGGER.info(name + " started");
    }

    public void shutdown() {
        LOGGER.info("Starting to shutdown " + name);
        for (final Thread worker : workers) {
            worker.interrupt();
            try {
                worker.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info(name + " was shutdown");
    }

}
