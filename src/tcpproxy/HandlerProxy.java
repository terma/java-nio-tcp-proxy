package tcpproxy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class HandlerProxy implements Handler {

    private static final boolean DEBUG = false;

    @Override
    public void process(SelectionKey key) throws IOException {
        Holder holder = (Holder) key.attachment();
        if (holder == null) return;

        if (key.channel() == holder.clientChannel) {
            if (key.isValid() && key.isReadable()) readFromClient(key, holder);
            if (key.isValid() && key.isWritable()) writeToClient(key, holder);
        }

        if (key.channel() == holder.serverChannel) {
            if (key.isValid() && key.isReadable()) readFromServer(key, holder);
            if (key.isValid() && key.isWritable()) writeToServer(key, holder);
        }
    }

    private void readFromServer(SelectionKey key, Holder holder) throws IOException {
        int read = holder.serverChannel.read(holder.clientBuffer.getBuffer());

        if (read == -1) {
            if (DEBUG) System.out.println("Server decides to close.");
            holder.close();
        } else if (read > 0) {
            holder.clientBuffer.readyToRead();
            holder.register(key.selector());
            if (DEBUG) System.out.println("Red from server " + read + " bytes");
        }
    }

    private void writeToClient(SelectionKey key, Holder holder) throws IOException {
        writeTo(key, holder, holder.clientChannel, holder.clientBuffer, "client");
    }

    private void writeToServer(SelectionKey key, Holder holder) throws IOException {
        writeTo(key, holder, holder.serverChannel, holder.serverBuffer, "server");
    }

    private void writeTo(SelectionKey key, Holder holder, SocketChannel channel, Buffer buffer, String side) throws IOException {
        int write = channel.write(buffer.getBuffer());
        if (write > 0) {
            buffer.readyToWrite();
            holder.register(key.selector());
            if (DEBUG) System.out.println("Wrote to " + side + " " + write + " bytes");
        }
    }

    private void readFromClient(SelectionKey key, Holder holder) throws IOException {
        // client -> buffer
        int read = holder.clientChannel.read(holder.serverBuffer.getBuffer());

        if (read == -1) {
            if (DEBUG) System.out.println("Client decides to close.");
            holder.close();
        } else if (read > 0) {
            holder.serverBuffer.readyToRead();
            holder.register(key.selector());
            if (DEBUG) System.out.println("Red from client " + read + " bytes");
        }
    }

}