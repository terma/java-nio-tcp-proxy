package tcpproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class HandlerAccept implements Handler {

    private final Selector selector;

    public HandlerAccept(final Selector newSelector) {
        selector = newSelector;
    }

    @Override
    public void process(final SelectionKey key) throws IOException {
        if (key.isValid() && key.isAcceptable()) {
            final ServerSocketChannel server = (ServerSocketChannel) key.channel();
            final SocketChannel client = server.accept();
            client.configureBlocking(false);

            InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8000);
            SocketChannel serverChannel = SocketChannel.open();
            serverChannel.connect(socketAddress);
            serverChannel.configureBlocking(false);

            HandlerProxy.Holder holder = new HandlerProxy.Holder(client, serverChannel);

            client.register(selector, SelectionKey.OP_READ, holder);
            serverChannel.register(selector, SelectionKey.OP_READ, holder);
        }
    }

}
