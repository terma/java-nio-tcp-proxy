package com.github.javaniotcpproxy.handler;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpProxyHandlerAcceptor implements TcpProxyHandler {

    private final Selector[] selectors;
    private final TcpProxyConfig config;
    private int selectorIndex;

    public TcpProxyHandlerAcceptor(final Selector[] selectors, TcpProxyConfig config) {
        this.selectors = selectors;
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

            final TcpProxyHandlerConnector handler = new TcpProxyHandlerConnector(
                    selectors[selectorIndex], clientChannel, serverChannel);
            handler.register();

            selectorIndex++;
            if (selectorIndex >= selectors.length) selectorIndex = 0;
        }
    }

}
