package com.github.terma.javaniotcpserver;

import org.junit.Test;
import org.mockito.Mockito;

public class TcpServerTest {

    private final TcpServerHandlerFactory handlerFactory = Mockito.mock(TcpServerHandlerFactory.class);
    private TcpServerConfig config = new TcpServerConfig(0, handlerFactory, 1);

    @Test
    public void shouldSuccessStartAndShutdown() {
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessStartAndShutdownWithThreeWorkers() {
        config = new TcpServerConfig(0, handlerFactory, 3);
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownTwice() {
        TcpServer connector = new TcpServer(config);

        connector.start();
        connector.shutdown();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownWithoutStart() {
        TcpServer connector = new TcpServer(config);
        connector.shutdown();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailWhenStartTwice() {
        TcpServer connector = new TcpServer(config);
        connector.start();

        try {
            connector.start();
        } finally {
            connector.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWhenCreateWithNullConfig() {
        new TcpServer(null);
    }

}
