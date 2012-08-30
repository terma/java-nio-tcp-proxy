package tcpproxy;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Holder {

    private static final boolean DEBUG = false;

    public final SocketChannel clientChannel;
    public final SocketChannel serverChannel;

    public final Buffer serverBuffer = new Buffer();
    public final Buffer clientBuffer = new Buffer();

    public Holder(SocketChannel clientChannel, SocketChannel serverChannel) {
        this.clientChannel = clientChannel;
        this.serverChannel = serverChannel;
    }

    public void close() throws IOException {
        clientChannel.close();
        serverChannel.close();

        if (DEBUG) System.out.println("Holder was closed.");
    }

    public void register(Selector selector) throws ClosedChannelException {
        int clientOps = 0;
        if (serverBuffer.getState() == BufferState.READY_TO_WRITE) clientOps |= SelectionKey.OP_READ;
        if (clientBuffer.getState() == BufferState.READY_TO_READ) clientOps |= SelectionKey.OP_WRITE;
        clientChannel.register(selector, clientOps, this);

        int serverOps = 0;
        if (clientBuffer.getState() == BufferState.READY_TO_WRITE) serverOps |= SelectionKey.OP_READ;
        if (serverBuffer.getState() == BufferState.READY_TO_READ) serverOps |= SelectionKey.OP_WRITE;
        serverChannel.register(selector, serverOps, this);

        if (DEBUG) System.out.println("Holder was registered client " + clientOps + " server " + serverOps);
    }

}
