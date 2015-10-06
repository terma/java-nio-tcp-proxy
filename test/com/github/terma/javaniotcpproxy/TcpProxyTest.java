package com.github.terma.javaniotcpproxy;

import org.junit.Test;

public class TcpProxyTest {

    private StaticTcpProxyConfig config = new StaticTcpProxyConfig(0, "", 0);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateWithNegativeWorkersCount() {
        config.setWorkerCount(-100);

        new TcpProxy(config);
    }

}
