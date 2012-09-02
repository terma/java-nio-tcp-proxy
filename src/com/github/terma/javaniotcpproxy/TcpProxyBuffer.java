/*
Copyright 2012 Artem Stasuk

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.terma.javaniotcpproxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

class TcpProxyBuffer {

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
        if (read == -1) throw new ClosedChannelException();

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
