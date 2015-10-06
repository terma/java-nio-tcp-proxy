/*
Copyright 2012 Artem Stasuk

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.terma.javaniotcpproxy;

import com.github.terma.javaniotcpserver.TcpServer;
import com.github.terma.javaniotcpserver.TcpServerConfig;

/**
 * TCP proxy.
 *
 * After starting it listening local port and send all incoming
 * traffic on it from client to remote host and from remote host to client.
 * Doesn't have any timeout. If client or remote server closes connection it will
 * close opposite connection.
 *
 * Multi-thread and asynchronous TCP proxy server based on NIO.
 *
 * You can create any count of proxy instances and run they in together.
 *
 * @see TcpProxyConnectorFactory
 * @see TcpProxyConnector
 * @see StaticTcpProxyConfig
 * @see TcpServer
 */
public class TcpProxy {

    private final TcpServer server;

    public TcpProxy(final TcpProxyConfig config) {
        TcpProxyConnectorFactory handlerFactory = new TcpProxyConnectorFactory(config);

        final TcpServerConfig serverConfig =
                new TcpServerConfig(config.getLocalPort(), handlerFactory, config.getWorkerCount());

        server = new TcpServer(serverConfig);
    }

    /**
     * Start server.
     * This method run servers worked then return control.
     * This method isn't blocking.
     *
     * If you call this method when server is started, it throw exception.
     *
     * See {@link com.github.terma.javaniotcpserver.TcpServer#start()}
     */
    public void start() {
        server.start();
    }

    /**
     * Stop server and release all resources.
     * If server already been closed this method return immediately
     * without side effects.
     *
     * See {@link com.github.terma.javaniotcpserver.TcpServer#shutdown()}
     */
    public void shutdown() {
        server.shutdown();
    }

}
