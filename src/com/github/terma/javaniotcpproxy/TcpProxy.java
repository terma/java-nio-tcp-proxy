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
 * Use {@link TcpServer} for create proxy server based on NIO
 * with multi-thread processing.
 *
 * @see TcpProxyConnectorFactory
 * @see TcpProxyConnector
 */
public class TcpProxy {

    private final TcpServer server;

    public TcpProxy(final TcpProxyConfig config) {
        final TcpServerConfig serverConfig = new TcpServerConfig(config.getLocalPort());
        serverConfig.setFactory(new TcpProxyConnectorFactory(config));
        serverConfig.setWorkerCount(config.getWorkerCount());

        server = new TcpServer(serverConfig);
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }

}
