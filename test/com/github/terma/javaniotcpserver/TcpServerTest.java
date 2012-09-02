package com.github.terma.javaniotcpserver;

import org.junit.Test;

import java.io.IOException;

public class TcpServerTest {

    private final TcpServerConfig config = new TcpServerConfig(0);

    @Test
    public void shouldSuccessStartAndShutdown() throws IOException {
        config.setWorkerCount(1);
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessStartAndShutdownWithThreeWorkers() throws IOException {
        config.setWorkerCount(3);
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownTwice() throws IOException {
        config.setWorkerCount(1);
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownWithoutStart() throws IOException {
        TcpServer connector = new TcpServer(config);
        connector.shutdown();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailWhenStartTwice() throws IOException {
        config.setWorkerCount(1);
        TcpServer connector = new TcpServer(config);
        connector.start();

        try {
            connector.start();
        } finally {
            connector.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWhenCreateWithNullConfig() throws IOException {
        new TcpServer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenStartWithWorkerCountsZero() throws IOException {
        config.setWorkerCount(0);
        new TcpServer(config).start();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenStartWithWorkerCountsNegative() throws IOException {
        config.setWorkerCount(-10);
        new TcpServer(config).start();
    }

}
