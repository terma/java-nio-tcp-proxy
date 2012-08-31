package com.github.configuration;

import java.util.*;

public class ConfigurationParser {

    @SuppressWarnings("unchecked")
    public static List<Configuration> parse(Properties properties) {
        final Set<String> proxyNames = new HashSet<String>();
        final List<String> propertySet = (List<String>) Collections.list(properties.propertyNames());
        for (final String propertyName : propertySet) {
            final int dotIndex = propertyName.lastIndexOf('.');
            if (dotIndex == -1) throw new IllegalArgumentException(
                    "Invalid property " + propertyName + " should be <proxy name>.localPort|remotePort|remoteHost");

            proxyNames.add(propertyName.substring(0, dotIndex));
        }
        if (proxyNames.isEmpty()) throw new IllegalArgumentException("Please specify at least one proxy.");

        final List<Configuration> configurations = new ArrayList<Configuration>();
        for (final String proxyName : proxyNames) {
            final int localPort = findIntegerProperty(properties, proxyName + ".localPort");
            final int remotePort = findIntegerProperty(properties, proxyName + ".remotePort");
            final String remoteHost = findProperty(properties, proxyName + ".remoteHost");

            configurations.add(new Configuration(localPort, remoteHost, remotePort));
        }
        return configurations;
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
