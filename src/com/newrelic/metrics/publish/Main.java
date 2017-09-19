package com.newrelic.metrics.publish;

import com.newrelic.metrics.publish.configuration.ConfigurationException;

public class Main {

    public static void main(final String[] args) {
        try {
            final Runner runner = new Runner();
            runner.add(new MacAgentFactoryImpl());
            runner.setupAndRun(); // Never returns
        } catch (final ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
