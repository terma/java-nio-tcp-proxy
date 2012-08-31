package com.github.javaniotcpproxy.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpProxyBuffer {

    private static enum BufferState {

        READY_TO_WRITE, READY_TO_READ

    }

    private final static int BUFFER_SIZE = 1000;

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private BufferState state = BufferState.READY_TO_WRITE;

    public boolean isReadyToRead() {
        return state == BufferState.READY_TO_READ;
    }

    public boolean isReadyToWrite() {
        return state == BufferState.READY_TO_WRITE;
    }

    public void writeFrom(SocketChannel channel) throws IOException {
        int read = channel.read(buffer);
        if (read == -1) throw new IOException(channel + " decided to close connection.");

        if (read > 0) {
            buffer.flip();
            state = BufferState.READY_TO_READ;
        }
    }

    /**
     * This method try to write data from buffer to channel.
     * Buffer changes state to READY_TO_READ only if all data were wrote to channel,
     * in other case you should call this method again
     *
     * @param channel - channel
     * @throws IOException
     */
    public void writeTo(SocketChannel channel) throws IOException {
        channel.write(buffer);

        // only if buffer is empty
        if (buffer.remaining() == 0) {
            buffer.clear();
            state = BufferState.READY_TO_WRITE;
        }
    }

}
