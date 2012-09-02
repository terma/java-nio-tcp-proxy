package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;
import org.junit.Test;

import java.io.IOException;

public class TcpProxyConnectorTest {

    private final TcpProxyConfig config = new TcpProxyConfig(0, "x", 0);

    @Test
    public void shouldSuccessStartAndShutdown() throws IOException {
        TcpProxyConnector connector = new TcpProxyConnector(config);
        connector.start(1);
        connector.shutdown();
    }

    @Test
    public void shouldSuccessStartAndShutdownWithThreeWorkers() throws IOException {
        TcpProxyConnector connector = new TcpProxyConnector(config);
        connector.start(3);
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownTwice() throws IOException {
        TcpProxyConnector connector = new TcpProxyConnector(config);
        connector.start(1);
        connector.shutdown();
        connector.shutdown();
    }

    @Test
    public void shouldSuccessShutdownWithoutStart() throws IOException {
        TcpProxyConnector connector = new TcpProxyConnector(config);
        connector.shutdown();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailWhenStartTwice() throws IOException {
        TcpProxyConnector connector = new TcpProxyConnector(config);
        connector.start(1);

        try {
            connector.start(1);
        } finally {
            connector.shutdown();
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWhenCreateWithNullConfig() throws IOException {
        new TcpProxyConnector(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenStartWithWorkerCountsZero() throws IOException {
        new TcpProxyConnector(config).start(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenStartWithWorkerCountsNegative() throws IOException {
        new TcpProxyConnector(config).start(-10);
    }

}
