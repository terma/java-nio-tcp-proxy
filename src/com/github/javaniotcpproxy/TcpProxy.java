package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;
import com.github.javaniotcpproxy.handler.TcpProxyHandler;
import com.github.javaniotcpproxy.handler.TcpProxyHandlerAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class TcpProxy {

    private final static Logger LOGGER = Logger.getAnonymousLogger();

    public void start(final List<TcpProxyConfig> configs) throws IOException {
        LOGGER.info("Starting tcp proxy");
        for (final TcpProxyConfig config : configs) {
            final ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(config.getLocalPort()));
            server.configureBlocking(false);

            final Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT, new TcpProxyHandlerAcceptor(selector, config));

            new Thread(new Runnable() {

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

            }).start();
            LOGGER.info("TcpProxy connector started to " + config.getRemoteHost() + ":" + config.getRemotePort()
                    + " from local port " + config.getLocalPort());
        }
        LOGGER.info("TcpProxy started");
    }

}
