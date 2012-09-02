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

import com.github.terma.javaniotcpserver.TcpServerHandler;
import com.github.terma.javaniotcpserver.TcpServerHandlerFactory;

import java.nio.channels.SocketChannel;

class TcpProxyConnectorFactory implements TcpServerHandlerFactory {

    private final TcpProxyConfig config;

    public TcpProxyConnectorFactory(TcpProxyConfig config) {
        this.config = config;
    }

    @Override
    public TcpServerHandler create(final SocketChannel clientChannel) {
        return new TcpProxyConnector(clientChannel, config);
    }

}
