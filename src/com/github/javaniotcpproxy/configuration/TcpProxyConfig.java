package com.github.javaniotcpproxy.configuration;

public class TcpProxyConfig {

    private final int localPort;
    private final String remoteHost;
    private final int remotePort;

    public TcpProxyConfig(int localPort, String remoteHost, int remotePort) {
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

}
