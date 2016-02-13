package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.invoke.JsonParameterVariables;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public class LambdaEventSourceVariables extends AbstractDescribableImpl<LambdaEventSourceVariables> {
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private Secret awsSecretKey;
    private String awsRegion;
    private String functionName;
    private String functionAlias;
    private String eventSourceArn;
    private boolean successOnly;

    @DataBoundConstructor
    public LambdaEventSourceVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String functionAlias, String eventSourceArn, boolean successOnly) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.functionAlias = functionAlias;
        this.eventSourceArn = eventSourceArn;
        this.successOnly = successOnly;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public Secret getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(Secret awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getFunctionAlias() {
        return functionAlias;
    }

    public void setFunctionAlias(String functionAlias) {
        this.functionAlias = functionAlias;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    public void setUseInstanceCredentials(boolean useInstanceCredentials) {
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public String getEventSourceArn() {
        return eventSourceArn;
    }

    public void setEventSourceArn(String eventSourceArn) {
        this.eventSourceArn = eventSourceArn;
    }

    public boolean getSuccessOnly() {
        return successOnly;
    }

    public void setSuccessOnly(boolean successOnly) {
        this.successOnly = successOnly;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        awsSecretKey = Secret.fromString(expand(Secret.toString(awsSecretKey), env));
        awsRegion = expand(awsRegion, env);
        functionName = expand(functionName, env);
        functionAlias = expand(functionAlias, env);
        eventSourceArn = expand(eventSourceArn, env);
    }

    private String expand(String value, EnvVars env) {
        return Util.replaceMacro(value.trim(), env);
    }

    public EventSourceConfig getEventSourceConfig() {
        return new EventSourceConfig(functionName, functionAlias, eventSourceArn);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion);
        } else {
            return new LambdaClientConfig(awsAccessKeyId, Secret.toString(awsSecretKey), awsRegion);
        }
    }

    public LambdaEventSourceVariables getClone() {
        return new LambdaEventSourceVariables(useInstanceCredentials, awsAccessKeyId, awsSecretKey, awsRegion, functionName, functionAlias, eventSourceArn, successOnly);
    }

    @Extension
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaEventSourceVariables> {
        public String getDisplayName() {
            return "Add Event Source Mapping";
        }
    }
}
