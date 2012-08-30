package tcpproxy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutionException;

public class Runner {

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException {

        final ServerSocketChannel server = ServerSocketChannel.open();
        server.socket().bind(new java.net.InetSocketAddress(7000));
        server.configureBlocking(false);

        final Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        final Dispatcher dispatcher = new DispatcherSameThread(selector);
//        final Dispatcher dispatcher = new DispatcherThreadPool(selector, args[0]);
//        final Dispatcher dispatcher = new DispatcherShareSelector(selector);
        dispatcher.start();
    }

}
