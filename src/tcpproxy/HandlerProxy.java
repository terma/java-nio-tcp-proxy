package tcpproxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class HandlerProxy implements Handler {

    @Override
    public void process(SelectionKey key) throws IOException {
        Holder holder = (Holder) key.attachment();
        if (holder == null) return;

        if (key.channel() == holder.clientChannel) {
            if (key.isValid() && key.isReadable()) {
                // client -> buffer
                int read = holder.clientChannel.read(holder.serverBuffer);

                if (read == -1) {
                    System.out.println("Client decides to close.");
                    holder.close();
                } else {
                    holder.clientBuffer.flip();
                    holder.serverBufferState = BufferState.READY_TO_READ;
                    holder.register(key.selector());
                }
            }

            if (key.isValid() && key.isWritable()) {
                // buffer -> client
                holder.clientChannel.write(holder.clientBuffer);
                holder.clientBuffer.clear();
                holder.clientBufferState = BufferState.READY_TO_WRITE;
                holder.register(key.selector());
            }
        }

        if (key.channel() == holder.serverChannel) {
            if (key.isValid() && key.isReadable()) {
                int read = holder.serverChannel.read(holder.clientBuffer);

                if (read == -1) {
                    System.out.println("Server decides to close.");
                    holder.close();
                } else {
                    holder.clientBuffer.flip();
                    holder.clientBufferState = BufferState.READY_TO_READ;
                    holder.register(key.selector());
                }
            }

            if (key.isValid() && key.isWritable()) {
                holder.serverChannel.write(holder.serverBuffer);
                holder.serverBuffer.clear();
                holder.serverBufferState = BufferState.READY_TO_WRITE;
                holder.register(key.selector());
            }
        }
    }

    public static class Holder {

        public SocketChannel clientChannel;
        public SocketChannel serverChannel;

        public ByteBuffer serverBuffer;
        public BufferState serverBufferState;

        public ByteBuffer clientBuffer;
        public BufferState clientBufferState;

        public void close() throws IOException {
            clientChannel.close();
            serverChannel.close();

            System.out.println("Holder was closed.");
        }

        public void register(Selector selector) throws ClosedChannelException {
            int clientOps = 0;
            if (serverBufferState == BufferState.READY_TO_WRITE) clientOps |= SelectionKey.OP_READ;
            if (clientBufferState == BufferState.READY_TO_READ) clientOps |= SelectionKey.OP_WRITE;
            clientChannel.register(selector, clientOps, this);

            int serverOps = 0;
            if (clientBufferState == BufferState.READY_TO_WRITE) serverOps |= SelectionKey.OP_READ;
            if (serverBufferState == BufferState.READY_TO_READ) serverOps |= SelectionKey.OP_WRITE;
            serverChannel.register(selector, serverOps, this);

            System.out.println("Holder was registered client " + clientOps + " server " + serverOps);
        }

    }

    public static enum BufferState {

        READY_TO_WRITE, READY_TO_READ

    }

}