package com.github.terma.javaniotcpproxy;

import com.github.terma.javaniotcpserver.TcpServerHandler;
import com.github.terma.javaniotcpserver.TcpServerHandlerFactory;

import java.nio.channels.SocketChannel;

class TcpProxyConnectorFactory implements TcpServerHandlerFactory {

    private final TcpProxyConfig config;

    public TcpProxyConnectorFactory(TcpProxyConfig config) {
        this.config = config;
    }

    @Override
    public TcpServerHandler create(final SocketChannel clientChannel) {
        return new TcpProxyConnector(clientChannel, config);
    }

}
