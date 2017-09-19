package com.newrelic.metrics.publish;

import com.newrelic.metrics.publish.configuration.ConfigurationException;

import java.util.Map;

public class MacAgentFactoryImpl extends AgentFactory {
    @Override
    public Agent createConfiguredAgent(final Map<String, Object> properties) throws ConfigurationException {
        final String name = (String) properties.get("name");

        if (name == null) {
            throw new ConfigurationException("'name' cannot be null.");
        }

        return new MacAgent(name);
    }
}
