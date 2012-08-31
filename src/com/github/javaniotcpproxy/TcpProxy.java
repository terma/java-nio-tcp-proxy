package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;
import com.github.javaniotcpproxy.handler.TcpProxyHandler;
import com.github.javaniotcpproxy.handler.TcpProxyHandlerAccept;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.List;
import java.util.Set;

public class TcpProxy {

    public void start(final List<TcpProxyConfig> configs) throws IOException {
        for (final TcpProxyConfig config : configs) {
            final ServerSocketChannel server = ServerSocketChannel.open();
            server.socket().bind(new InetSocketAddress(config.getLocalPort()));
            server.configureBlocking(false);

            final Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT, new TcpProxyHandlerAccept(selector, config));

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
        }
    }

}
