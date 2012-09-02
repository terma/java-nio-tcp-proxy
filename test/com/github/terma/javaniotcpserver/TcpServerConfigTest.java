package com.github.terma.javaniotcpserver;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TcpServerConfigTest {

    private final TcpServerHandlerFactory handlerFactory = Mockito.mock(TcpServerHandlerFactory.class);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateWithWorkerCountsZero() {
        new TcpServerConfig(0, handlerFactory, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateWithNegativeWorkerCount() {
        new TcpServerConfig(0, handlerFactory, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenCreateWithNegativePort() {
        new TcpServerConfig(-90, handlerFactory, 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWhenCreateWithNullHandlerFactory() {
        new TcpServerConfig(0, null, 3);
    }

    @Test
    public void shouldSuccess() {
        final TcpServerConfig config = new TcpServerConfig(5600, handlerFactory, 3);

        Assert.assertEquals(5600, config.getPort());
        Assert.assertSame(handlerFactory, config.getHandlerFactory());
        Assert.assertEquals(3, config.getWorkerCount());
    }

}
