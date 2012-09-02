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

package com.github.terma.javaniotcpserver;

import java.nio.channels.SocketChannel;

/**
 * TCP server uses this class for create handler for
 * all incoming connection from clients. When handler
 * was created TCP server use it for process events from client channel.
 */
public interface TcpServerHandlerFactory {

    TcpServerHandler create(SocketChannel clientChannel);

}
