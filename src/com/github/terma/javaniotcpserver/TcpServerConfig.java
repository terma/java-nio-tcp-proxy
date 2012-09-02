package com.github.terma.javaniotcpserver;

public class TcpServerConfig {

    private int port;
    private int workerCount;
    private TcpServerHandlerFactory factory;

    public TcpServerConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public TcpServerHandlerFactory getFactory() {
        return factory;
    }

    public void setFactory(TcpServerHandlerFactory factory) {
        this.factory = factory;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

}
