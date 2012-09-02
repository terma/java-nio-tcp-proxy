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

/**
 * Configuration for TCP server
 */
public class TcpServerConfig {

    private final int port;
    private final int workerCount;
    private final TcpServerHandlerFactory handlerFactory;

    public TcpServerConfig(int port, TcpServerHandlerFactory handlerFactory, int workerCount) {
        if (workerCount < 1)
            throw new IllegalArgumentException("Count of workers should be at least 1!");

        if (port < 0)
            throw new IllegalArgumentException("Port can't be negative!");

        if (handlerFactory == null)
            throw new NullPointerException("Please specify handler factory!");

        this.port = port;
        this.workerCount = workerCount;
        this.handlerFactory = handlerFactory;
    }

    /**
     * @return - local port which TCP server will be listening, should be 0..64000
     */
    public int getPort() {
        return port;
    }

    /**
     * @return - handler factory which TCP server will use for process incoming connections
     */
    public TcpServerHandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    /**
     * @return - count of worker (thread) which TCP proxy will use for processing
     *         incoming client connection, should more 0
     */
    public int getWorkerCount() {
        return workerCount;
    }

}
