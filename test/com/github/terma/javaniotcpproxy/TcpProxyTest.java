package com.github.terma.javaniotcpproxy;

import org.junit.Test;

public class TcpProxyTest {

    private TcpProxyConfig config = new TcpProxyConfig(0, "", 0);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateWithNegativeWorkersCount() {
        config.setWorkerCount(-100);

        new TcpProxy(config);
    }

}
