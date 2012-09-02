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

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class TcpServerWorker extends Thread {

    private final static long SELECTOR_TIMEOUT = 100L;
    private final static Logger LOGGER = Logger.getAnonymousLogger();

    private final Queue<TcpServerHandler> handlers;

    public TcpServerWorker(final Queue<TcpServerHandler> handlers) {
        super("TcpServerWorker");
        this.handlers = handlers;
    }

    @Override
    public void run() {
        Selector selector = null;
        try {
            selector = Selector.open();

            while (!Thread.interrupted()) {
                TcpServerHandler newHandler = handlers.poll();
                if (newHandler != null) {
                    newHandler.register(selector);
                }

                selector.select(SELECTOR_TIMEOUT);

                final Set<SelectionKey> keys = selector.selectedKeys();
                for (final SelectionKey key : keys) {
                    final TcpServerHandler handler = (TcpServerHandler) key.attachment();
                    handler.process(key);
                }
                keys.clear();
            }
        } catch (final IOException exception) {
            if (LOGGER.isLoggable(Level.SEVERE))
                LOGGER.log(Level.SEVERE, "Problem with selector, worker will be stopped!", exception);
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException exception) {
                    if (LOGGER.isLoggable(Level.WARNING))
                        LOGGER.log(Level.WARNING, "Could not close selector properly.", exception);
                }
            }
        }
    }

}
