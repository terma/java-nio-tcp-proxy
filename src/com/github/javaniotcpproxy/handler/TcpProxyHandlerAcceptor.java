package com.github.javaniotcpproxy.handler;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpProxyHandlerAcceptor implements TcpProxyHandler {

    private final Selector selector;
    private final TcpProxyConfig config;

    public TcpProxyHandlerAcceptor(final Selector newSelector, TcpProxyConfig config) {
        selector = newSelector;
        this.config = config;
    }

    @Override
    public void process(final SelectionKey key) throws IOException {
        if (key.isValid() && key.isAcceptable()) {
            final ServerSocketChannel server = (ServerSocketChannel) key.channel();
            final SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            final InetSocketAddress socketAddress = new InetSocketAddress(
                    config.getRemoteHost(), config.getRemotePort());
            final SocketChannel serverChannel = SocketChannel.open();
            serverChannel.connect(socketAddress);
            serverChannel.configureBlocking(false);

            final TcpProxyHandlerConnector handler =
                    new TcpProxyHandlerConnector(selector, clientChannel, serverChannel);
            handler.register();
        }
    }

}
