package tcpproxy;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.CharacterCodingException;
import java.util.Set;

public class DispatcherSameThread implements Dispatcher {

    private final Handler handler;
    private final Selector selector;

    public DispatcherSameThread(final Selector newSelector) throws CharacterCodingException {
        handler = new HandlerComposite(new HandlerAccept(newSelector), new HandlerProxy());
        selector = newSelector;
    }

    @Override
    public void start() throws IOException {
        //noinspection InfiniteLoopStatement
        for (; ; ) {
            selector.select();

            final Set<SelectionKey> keys = selector.selectedKeys();
            for (final SelectionKey key : keys) {
                handler.process(key);
            }
            keys.clear();
        }
    }

}
