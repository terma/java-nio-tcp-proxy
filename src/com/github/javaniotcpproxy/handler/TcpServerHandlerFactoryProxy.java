package com.github.javaniotcpproxy.handler;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;

import java.nio.channels.SocketChannel;

public class TcpServerHandlerFactoryProxy implements TcpServerHandlerFactory {

    private final TcpProxyConfig config;

    public TcpServerHandlerFactoryProxy(TcpProxyConfig config) {
        this.config = config;
    }

    @Override
    public TcpProxyHandler create(final SocketChannel clientChannel) {
        return new TcpProxyHandlerConnector(clientChannel, config);
    }

}
