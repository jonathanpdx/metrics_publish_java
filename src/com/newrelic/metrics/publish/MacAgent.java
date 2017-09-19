package com.newrelic.metrics.publish;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MacAgent extends Agent {

    private static final String GUID = "com.newrelic.testing.fakePlugin";
    private static final String VERSION = "1.0.0";
    private final String name;

    public MacAgent(final String name) {
        this(GUID, VERSION, name);
    }

    private MacAgent(final String GUID, final String version, final String name) {
        super(GUID, version);
        this.name = name;
    }

    @Override
    public void pollCycle() {
        final String[] cmd = {
                "/bin/sh",
                "-c",
                "ps -e | wc -l"
        };

        final String numProcs = runCommand(cmd);
        final int count = Integer.parseInt(numProcs);
        reportMetric("Processes/Count", "processes", count);

        // Added so we meet the minimum requirement
        reportMetric("AllProcesses/Count", "processes", count);
        reportMetric("ReallyAllProcesses/Count", "processes", count);

    }

    private String runCommand(final String[] commands) {
        String output = "";
        try {
            String line;
            final Process p = Runtime.getRuntime().exec(commands);
            final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                output += line;
            }
            input.close();
        } catch (final Exception err) {
            err.printStackTrace();
        }
        return output.trim();
    }

    @Override
    public String getAgentName() {
        return name;
    }
}
