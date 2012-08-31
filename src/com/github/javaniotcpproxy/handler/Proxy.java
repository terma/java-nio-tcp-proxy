package com.github.javaniotcpproxy.handler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

class Proxy {

    private final Selector selector;

    private final Buffer clientBuffer = new Buffer();
    private final Buffer serverBuffer = new Buffer();

    public final SocketChannel clientChannel;
    public final SocketChannel serverChannel;

    public Proxy(Selector selector, SocketChannel clientChannel, SocketChannel serverChannel) {
        this.selector = selector;
        this.clientChannel = clientChannel;
        this.serverChannel = serverChannel;
    }

    public void readFromClient() throws IOException {
        serverBuffer.writeFrom(clientChannel);
        if (serverBuffer.isReadyToRead()) register();
    }

    public void readFromServer() throws IOException {
        clientBuffer.writeFrom(serverChannel);
        if (clientBuffer.isReadyToRead()) register();
    }

    public void writeToClient() throws IOException {
        clientBuffer.writeTo(clientChannel);
        if (clientBuffer.isReadyToWrite()) register();
    }

    public void writeToServer() throws IOException {
        serverBuffer.writeTo(serverChannel);
        if (serverBuffer.isReadyToWrite()) register();
    }

    public void close() {
        closeQuietly(clientChannel);
        closeQuietly(serverChannel);
    }

    public void register() throws ClosedChannelException {
        int clientOps = 0;
        if (serverBuffer.isReadyToWrite()) clientOps |= SelectionKey.OP_READ;
        if (clientBuffer.isReadyToRead()) clientOps |= SelectionKey.OP_WRITE;
        clientChannel.register(selector, clientOps, this);

        int serverOps = 0;
        if (clientBuffer.isReadyToWrite()) serverOps |= SelectionKey.OP_READ;
        if (serverBuffer.isReadyToRead()) serverOps |= SelectionKey.OP_WRITE;
        serverChannel.register(selector, serverOps, this);
    }

    private static void closeQuietly(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException exception) {
            // skip exception
        }
    }

}
