package com.xti.jenkins.plugin.awslambda.service;

import java.io.PrintStream;

public class JenkinsLogger {
    private PrintStream logStream;

    public JenkinsLogger(PrintStream logStream) {
        this.logStream = logStream;
    }

    public void log(String mask, Object... args) {
        if(mask != null) {
            logStream.println(String.format(mask, args));
        }
    }
}
