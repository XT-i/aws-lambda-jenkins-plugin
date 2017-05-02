package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.util.ExpansionUtils;
import com.xti.jenkins.plugin.awslambda.util.JenkinsProxy;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public class LambdaEventSourceVariables extends AbstractDescribableImpl<LambdaEventSourceVariables> {
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String clearTextAwsSecretKey;
    private String awsRegion;
    private String functionName;
    private String functionAlias;
    private String eventSourceArn;
    private boolean successOnly;

    @DataBoundConstructor
    public LambdaEventSourceVariables(String awsRegion, String functionName, String eventSourceArn) {
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.eventSourceArn = eventSourceArn;
    }

    @Deprecated
    public LambdaEventSourceVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String functionAlias, String eventSourceArn, boolean successOnly) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey != null ? awsSecretKey.getEncryptedValue() : null;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.functionAlias = functionAlias;
        this.eventSourceArn = eventSourceArn;
        this.successOnly = successOnly;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    @DataBoundSetter
    public void setUseInstanceCredentials(boolean useInstanceCredentials) {
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    @DataBoundSetter
    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    @DataBoundSetter
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = Secret.fromString(awsSecretKey).getEncryptedValue();
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public String getFunctionAlias() {
        return functionAlias;
    }

    @DataBoundSetter
    public void setFunctionAlias(String functionAlias) {
        this.functionAlias = functionAlias;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getEventSourceArn() {
        return eventSourceArn;
    }

    public boolean getSuccessOnly() {
        return successOnly;
    }

    @DataBoundSetter
    public void setSuccessOnly(boolean successOnly) {
        this.successOnly = successOnly;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = ExpansionUtils.expand(awsAccessKeyId, env);
        clearTextAwsSecretKey = ExpansionUtils.expand(Secret.toString(Secret.fromString(awsSecretKey)), env);
        awsRegion = ExpansionUtils.expand(awsRegion, env);
        functionName = ExpansionUtils.expand(functionName, env);
        functionAlias = ExpansionUtils.expand(functionAlias, env);
        eventSourceArn = ExpansionUtils.expand(eventSourceArn, env);
    }

    public EventSourceConfig getEventSourceConfig() {
        return new EventSourceConfig(functionName, functionAlias, eventSourceArn);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion, JenkinsProxy.getConfig());
        } else {
            return new LambdaClientConfig(awsAccessKeyId, clearTextAwsSecretKey, awsRegion, JenkinsProxy.getConfig());
        }
    }

    public LambdaEventSourceVariables getClone() {
        LambdaEventSourceVariables lambdaEventSourceVariables = new LambdaEventSourceVariables(awsRegion, functionName, eventSourceArn);
        lambdaEventSourceVariables.setUseInstanceCredentials(useInstanceCredentials);
        lambdaEventSourceVariables.setAwsAccessKeyId(awsAccessKeyId);
        lambdaEventSourceVariables.setAwsSecretKey(awsSecretKey);
        lambdaEventSourceVariables.setFunctionAlias(functionAlias);
        lambdaEventSourceVariables.setSuccessOnly(successOnly);
        return lambdaEventSourceVariables;
    }

    @Extension
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaEventSourceVariables> {
        public String getDisplayName() {
            return "Add Event Source Mapping";
        }
    }
}
