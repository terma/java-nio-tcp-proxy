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
            server.socket().bind(new InetSocketAddress(config.getPort()));
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

                handlers.add(config.getFactory().create(clientChannel));
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
