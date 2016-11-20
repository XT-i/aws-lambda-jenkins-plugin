package com.xti.jenkins.plugin.awslambda.upload;

import com.xti.jenkins.plugin.awslambda.util.ExpansionUtils;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class EnvironmentEntry extends AbstractDescribableImpl<EnvironmentEntry> {
    private String key;
    private String value;

    @DataBoundConstructor
    public EnvironmentEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void expandVariables(EnvVars env) {
        key = ExpansionUtils.expand(key, env);
        value = ExpansionUtils.expand(value, env);
    }

    public EnvironmentEntry getClone(){
        return new EnvironmentEntry(key, value);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<EnvironmentEntry> {
        public String getDisplayName() { return ""; }
    }
}
