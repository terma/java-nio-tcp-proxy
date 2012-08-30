package tcpproxy;

import java.nio.ByteBuffer;

class Buffer {

    private final static int BUFFER_SIZE = 1000;

    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private BufferState state = BufferState.READY_TO_WRITE;

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public BufferState getState() {
        return state;
    }

    public void readyToRead() {
        buffer.flip();
        state = BufferState.READY_TO_READ;
    }

    public void readyToWrite() {
        buffer.clear();
        state = BufferState.READY_TO_WRITE;
    }

}
