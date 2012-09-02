package com.github.javaniotcpproxy.handler;

import java.nio.channels.SocketChannel;

public interface TcpServerHandlerFactory {

    TcpProxyHandler create(SocketChannel clientChannel);

}
