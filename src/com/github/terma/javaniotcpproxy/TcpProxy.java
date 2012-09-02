package com.github.terma.javaniotcpproxy;

import com.github.terma.javaniotcpserver.TcpServer;
import com.github.terma.javaniotcpserver.TcpServerConfig;

public class TcpProxy {

    private final TcpServer server;

    public TcpProxy(final TcpProxyConfig config) {
        final TcpServerConfig serverConfig = new TcpServerConfig(config.getLocalPort());
        serverConfig.setFactory(new TcpProxyConnectorFactory(config));
        serverConfig.setWorkerCount(config.getWorkerCount());

        server = new TcpServer(serverConfig);
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }

}
