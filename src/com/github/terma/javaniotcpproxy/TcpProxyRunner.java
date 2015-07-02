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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpProxyRunner {

    private final static Logger LOGGER = Logger.getAnonymousLogger();

    public static void main(final String[] args) {
        if (args.length != 1) {
            System.err.println("Please specify path to config file!");
            System.exit(1);
        }

        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(args[0]));
        } catch (IOException exception) {
            if (LOGGER.isLoggable(Level.SEVERE))
                LOGGER.log(Level.SEVERE, "Can't load properties from " + args[0], exception);
            System.exit(1);
        }

        final List<TcpProxyConfig> configs = TcpProxyConfigParser.parse(properties);
        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("Starting TcpProxy with " + configs.size() + " connectors");

        final int cores = Runtime.getRuntime().availableProcessors();
        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("TcpProxy detected " + cores + " core" + (cores > 1 ? "s" : ""));

        final int workerCount = Math.max(cores / configs.size(), 1);
        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("TcpProxy will use " + workerCount + " workers per connector");

        for (final TcpProxyConfig config : configs) {
            config.setWorkerCount(workerCount);
            new TcpProxy(config).start();
        }

        if (LOGGER.isLoggable(Level.INFO))
            LOGGER.info("TcpProxy started");
    }

}
