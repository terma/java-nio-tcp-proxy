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

import java.util.*;

public class TcpProxyConfigParser {

    @SuppressWarnings("unchecked")
    public static List<TcpProxyConfig> parse(Properties properties) {
        final Set<String> proxyNames = new HashSet<String>();
        final List<String> propertySet = (List<String>) Collections.list(properties.propertyNames());
        for (final String propertyName : propertySet) {
            final int dotIndex = propertyName.lastIndexOf('.');
            if (dotIndex == -1) throw new IllegalArgumentException(
                    "Invalid property " + propertyName + " should be <proxy name>.localPort|remotePort|remoteHost");

            proxyNames.add(propertyName.substring(0, dotIndex));
        }
        if (proxyNames.isEmpty()) throw new IllegalArgumentException("Please specify at least one proxy.");

        final List<TcpProxyConfig> tcpProxyConfigs = new ArrayList<TcpProxyConfig>();
        for (final String proxyName : proxyNames) {
            final int localPort = findIntegerProperty(properties, proxyName + ".localPort");
            final int remotePort = findIntegerProperty(properties, proxyName + ".remotePort");
            final String remoteHost = findProperty(properties, proxyName + ".remoteHost");

            tcpProxyConfigs.add(new StaticTcpProxyConfig(localPort, remoteHost, remotePort));
        }
        return tcpProxyConfigs;
    }

    private static int findIntegerProperty(Properties properties, String key) {
        final String value = findProperty(properties, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid integer " + key + " = " + value, exception);
        }
    }

    private static String findProperty(Properties properties, String key) {
        final String value = properties.getProperty(key);
        if (value == null) throw new IllegalArgumentException("Please specify " + key);
        return value;
    }

}
