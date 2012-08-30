package tcpproxy;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created with IntelliJ IDEA.
 * User: artem_000
 * Date: 8/29/12
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Handler {
    void process(SelectionKey key) throws IOException;
}
