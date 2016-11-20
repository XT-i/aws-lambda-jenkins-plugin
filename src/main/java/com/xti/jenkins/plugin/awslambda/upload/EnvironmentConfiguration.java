package com.xti.jenkins.plugin.awslambda.upload;

import com.xti.jenkins.plugin.awslambda.util.ExpansionUtils;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentConfiguration extends AbstractDescribableImpl<EnvironmentConfiguration> {

    private boolean configureEnvironment = false;
    private List<EnvironmentEntry> environment = new ArrayList<>();
    private String kmsArn;

    @DataBoundConstructor
    public EnvironmentConfiguration(List<EnvironmentEntry> environment){
        this.environment = environment;
    }

    public boolean isConfigureEnvironment() {
        return configureEnvironment;
    }

    @DataBoundSetter
    public void setConfigureEnvironment(boolean configureEnvironment) {
        this.configureEnvironment = configureEnvironment;
    }

    public List<EnvironmentEntry> getEnvironment() {
        return environment;
    }

    public Map<String, String> getMapRepresentation(){
        Map<String, String> environmentMap = new HashMap<>();
        for (EnvironmentEntry environmentEntry : environment) {
            environmentMap.put(environmentEntry.getKey(), environmentEntry.getValue());
        }
        return environmentMap;
    }

    public String getKmsArn() {
        return kmsArn;
    }

    @DataBoundSetter
    public void setKmsArn(String kmsArn) {
        this.kmsArn = kmsArn;
    }

    public void expandVariables(EnvVars env) {
        kmsArn = ExpansionUtils.expand(kmsArn, env);
        for (EnvironmentEntry environmentEntry : environment) {
            environmentEntry.expandVariables(env);
        }
    }

    public EnvironmentConfiguration getClone(){
        List<EnvironmentEntry> environmentEntries = new ArrayList<>();
        if(environment != null) {
            for (EnvironmentEntry environmentEntry : environment) {
                environmentEntries.add(environmentEntry.getClone());
            }
        }
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environmentEntries);
        environmentConfiguration.setConfigureEnvironment(configureEnvironment);
        environmentConfiguration.setKmsArn(kmsArn);
        return environmentConfiguration;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<EnvironmentConfiguration> {
        public String getDisplayName() { return ""; }
    }
}
