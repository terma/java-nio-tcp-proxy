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

package com.github.javaniotcpproxy.handler;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpProxyHandlerAcceptor implements TcpProxyHandler {

    private final Selector[] selectors;
    private final TcpProxyConfig config;
    private int selectorIndex;

    public TcpProxyHandlerAcceptor(final Selector[] selectors, TcpProxyConfig config) {
        this.selectors = selectors;
        this.config = config;
    }

    @Override
    public void process(final SelectionKey key) throws IOException {
        if (key.isValid() && key.isAcceptable()) {
            final ServerSocketChannel server = (ServerSocketChannel) key.channel();
            final SocketChannel clientChannel = server.accept();
            clientChannel.configureBlocking(false);

            final InetSocketAddress socketAddress = new InetSocketAddress(
                    config.getRemoteHost(), config.getRemotePort());
            final SocketChannel serverChannel = SocketChannel.open();
            serverChannel.connect(socketAddress);
            serverChannel.configureBlocking(false);

            final TcpProxyHandlerConnector handler = new TcpProxyHandlerConnector(
                    selectors[selectorIndex], clientChannel, serverChannel);
            handler.register();

            selectorIndex++;
            if (selectorIndex >= selectors.length) selectorIndex = 0;
        }
    }

}
