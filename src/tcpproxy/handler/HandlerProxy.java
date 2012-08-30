package tcpproxy.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class HandlerProxy implements Handler {

    @Override
    public void process(SelectionKey key) {
        Proxy proxy = (Proxy) key.attachment();
        if (proxy == null) return;

        try {
            if (key.channel() == proxy.clientChannel) {
                if (key.isValid() && key.isReadable()) proxy.readFromClient();
                if (key.isValid() && key.isWritable()) proxy.writeToClient();
            }

            if (key.channel() == proxy.serverChannel) {
                if (key.isValid() && key.isReadable()) proxy.readFromServer();
                if (key.isValid() && key.isWritable()) proxy.writeToServer();
            }
        } catch (IOException exception) {
            proxy.close();
        }
    }

}