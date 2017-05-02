package com.xti.jenkins.plugin.awslambda.util;

import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

public class JenkinsProxy {
    public static ProxyConfiguration getConfig(){
        Jenkins instance = Jenkins.getInstance();

        if (instance != null) {
            return instance.proxy;
        } else {
            return null;
        }
    }
}
