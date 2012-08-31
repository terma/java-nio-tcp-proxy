package com.github.javaniotcpproxy.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class HandlerComposite implements Handler {

    private final Handler[] handlers;

    public HandlerComposite(final Handler... handlers) {
        this.handlers = handlers;
    }

    @Override
    public void process(SelectionKey key) throws IOException {
        for (final Handler handler : handlers) {
            handler.process(key);
        }
    }

}
