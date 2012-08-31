package com.github.javaniotcpproxy.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface TcpProxyHandler {

    void process(SelectionKey key) throws IOException;

}
