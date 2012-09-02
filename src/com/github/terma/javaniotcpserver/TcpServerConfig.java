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

public class TcpServerConfig {

    private int port;
    private int workerCount;
    private TcpServerHandlerFactory factory;

    public TcpServerConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public TcpServerHandlerFactory getFactory() {
        return factory;
    }

    public void setFactory(TcpServerHandlerFactory factory) {
        this.factory = factory;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

}
