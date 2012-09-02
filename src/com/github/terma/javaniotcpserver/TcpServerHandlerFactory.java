package com.github.terma.javaniotcpserver;

import com.github.terma.javaniotcpserver.TcpServerHandler;

import java.nio.channels.SocketChannel;

public interface TcpServerHandlerFactory {

    TcpServerHandler create(SocketChannel clientChannel);

}
