package com.xti.jenkins.plugin.awslambda.invoke;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

public class LambdaOutputInjectionAction implements EnvironmentContributingAction {

    private String key;
    private String value;

    public LambdaOutputInjectionAction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        if (env != null && key != null && value != null) {
            env.put(key, value);
        }
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "LambdaOutputInjectAction";
    }

    @Override
    public String getUrlName() {
        return null;
    }
}
