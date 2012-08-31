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

package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TcpProxy {

    private final static Logger LOGGER = Logger.getAnonymousLogger();

    /**
     * This method start connector for each configuration from list.
     * <p/>
     * It try to use all available processes in system but it try to create only
     * one thread per processor, so for example if it should start 2 connector
     * on machine with 4 processes it will start each connector with 2 workers.
     * Just for avoid context switching for each processor.
     * <p/>
     * In other case if it should start 2 connector on machine with 1 processor,
     * it will create connector with one worker, so in this case you will have
     * 2 threads on one processor.
     *
     * @param configs - list of configs for tcp proxy connectors
     * @throws IOException
     */
    public void start(final List<TcpProxyConfig> configs) throws IOException {
        LOGGER.info("Starting tcp proxy with " + configs.size() + " connectors");

        final int cores = Runtime.getRuntime().availableProcessors();
        LOGGER.info("TcpProxy detected " + cores + " core" + (cores > 1 ? "s" : ""));
        final int workers = Math.max(cores / configs.size(), 1);
        LOGGER.info("TcpProxy will use " + workers + " workers per connector");

        final List<TcpProxyConnector> connectors = new ArrayList<TcpProxyConnector>();
        for (final TcpProxyConfig config : configs) {
            connectors.add(new TcpProxyConnector(config));
        }

        try {
            for (final TcpProxyConnector connector : connectors) {
                connector.start(workers);
            }
        } catch (final IOException exception) {
            // if smt. wrong let shutdown all started connectors!
            for (final TcpProxyConnector connector : connectors) {
                connector.shutdown();
            }
            throw exception;
        }

        LOGGER.info("TcpProxy started");
    }

}
