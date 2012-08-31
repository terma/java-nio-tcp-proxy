package com.github.javaniotcpproxy;

import com.github.javaniotcpproxy.configuration.TcpProxyConfigParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class TcpProxyRunner {

    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 1) {
            System.err.println("Please specify path to config file!");
            System.exit(1);
        }

        final Properties properties = new Properties();
        properties.load(new FileInputStream(args[0]));

        new TcpProxy().start(TcpProxyConfigParser.parse(properties));
    }

}
