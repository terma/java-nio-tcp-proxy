package com.github.terma.javaniotcpserver;

import com.github.terma.javaniotcpproxy.StaticTcpProxyConfig;
import com.github.terma.javaniotcpproxy.TcpProxy;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

    @Test
    public void shouldCloseProxySocketOnShutdown() throws IOException {
        EchoServer echoServer = new EchoServer();
        int serverPort = echoServer.startAndGetPort();

        int proxyPort = 12345;
        StaticTcpProxyConfig config = new StaticTcpProxyConfig(proxyPort, "localhost", serverPort);
        config.setWorkerCount(1);
        TcpProxy proxy = new TcpProxy(config);
        proxy.start();

        sendTextTo(proxyPort, "First message");

        proxy.shutdown();
        echoServer.shutdown();

        try {
            sendTextTo(proxyPort, "Second message");
            fail("Proxy was shutdown but server socket is still listening");
        } catch (IOException exception) {
            assertEquals("Connection refused (Connection refused)", exception.getMessage());
        }
    }

    private void sendTextTo(int localPort, String text) throws IOException {
        Socket socket = new Socket("localhost", localPort);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(text.getBytes());
        outputStream.flush();
        outputStream.close();
        socket.close();
    }

    private static class EchoServer {

        private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

        int startAndGetPort() throws IOException {
            CountDownLatch serverStarted = new CountDownLatch(1);
            ServerSocket serverSocket = new ServerSocket(0);
            threadPool.submit(new BlockingEcho(serverStarted, serverSocket));

            try {
                serverStarted.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return serverSocket.getLocalPort();
        }

        void shutdown() {
            threadPool.shutdown();
            try {
                threadPool.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private static class BlockingEcho implements Runnable {

        private final static Logger LOGGER = Logger.getAnonymousLogger();

        private final CountDownLatch serverStarted;
        private final ServerSocket serverSocket;

        public BlockingEcho(CountDownLatch serverStarted, ServerSocket serverSocket) {
            this.serverStarted = serverStarted;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                serverStarted.countDown();
                Socket socket = serverSocket.accept();
                int readBytesCount;
                byte[] bytes = new byte[1024];
                while ((readBytesCount = socket.getInputStream().read(bytes)) > -1) {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(Arrays.copyOfRange(bytes, 0, readBytesCount));
                    outputStream.flush();
                }
            } catch (final IOException exception) {
                LOGGER.log(Level.WARNING, "Could not echo message.", exception);
            }
        }
    }

}
