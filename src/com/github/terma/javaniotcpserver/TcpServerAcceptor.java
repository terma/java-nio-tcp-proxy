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
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

class TcpServerAcceptor implements TcpServerHandler {

    private final static int ACCEPT_BUFFER_SIZE = 1000;
    private final static Logger LOGGER = Logger.getAnonymousLogger();

    private final TcpServerConfig config;
    private final Queue<TcpServerHandler> handlers;

    public TcpServerAcceptor(final TcpServerConfig config, final Queue<TcpServerHandler> handlers) {
        this.config = config;
        this.handlers = handlers;
    }

    @Override
    public void register(final Selector selector) {
        try {
            final ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(config.getPort()), ACCEPT_BUFFER_SIZE);
            server.configureBlocking(false);
            server.register(selector, SelectionKey.OP_ACCEPT, this);
        } catch (final IOException exception) {
            if (LOGGER.isLoggable(Level.SEVERE))
                LOGGER.log(Level.SEVERE, "Can't init server connection!", exception);
        }
    }

    @Override
    public void process(SelectionKey key) {
        if (key.isValid() && key.isAcceptable()) {
            try {
                final ServerSocketChannel server = (ServerSocketChannel) key.channel();

                SocketChannel clientChannel;
                clientChannel = server.accept();

                handlers.add(config.getHandlerFactory().create(clientChannel));
            } catch (final IOException exception) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "Can't accept client connection!", exception);
            }
        }
    }

    @Override
    public void destroy() {
        // nothing
    }

}
