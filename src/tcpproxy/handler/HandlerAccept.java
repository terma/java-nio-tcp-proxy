package tcpproxy.handler;

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
            final SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8000);
            SocketChannel serverChannel = SocketChannel.open();
            serverChannel.connect(socketAddress);
            serverChannel.configureBlocking(false);

            Proxy proxy = new Proxy(selector, clientChannel, serverChannel);
            proxy.register();
        }
    }

}
