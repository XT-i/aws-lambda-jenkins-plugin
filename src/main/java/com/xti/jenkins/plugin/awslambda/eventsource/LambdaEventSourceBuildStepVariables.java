package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Created by anthonyikeda on 25/11/2015.
 *
 */
public class LambdaEventSourceBuildStepVariables extends AbstractDescribableImpl<LambdaEventSourceBuildStepVariables>{
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String clearTextAwsSecretKey;
    private String awsRegion;
    private String functionName;
    private String functionAlias;
    private String eventSourceArn;

    @DataBoundConstructor
    public LambdaEventSourceBuildStepVariables(String awsRegion, String functionName, String eventSourceArn) {
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.eventSourceArn = eventSourceArn;
    }

    @Deprecated
    public LambdaEventSourceBuildStepVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String functionAlias, String eventSourceArn) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey != null ? awsSecretKey.getEncryptedValue() : null;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.functionAlias = functionAlias;
        this.eventSourceArn = eventSourceArn;
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

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        clearTextAwsSecretKey = expand(Secret.toString(Secret.fromString(awsSecretKey)), env);
        awsRegion = expand(awsRegion, env);
        functionName = expand(functionName, env);
        functionAlias = expand(functionAlias, env);
        eventSourceArn = expand(eventSourceArn, env);
    }

    public LambdaEventSourceBuildStepVariables getClone() {
        LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables = new LambdaEventSourceBuildStepVariables(awsRegion, functionName, eventSourceArn);
        lambdaEventSourceBuildStepVariables.setUseInstanceCredentials(useInstanceCredentials);
        lambdaEventSourceBuildStepVariables.setAwsAccessKeyId(awsAccessKeyId);
        lambdaEventSourceBuildStepVariables.setAwsSecretKey(awsSecretKey);
        lambdaEventSourceBuildStepVariables.setFunctionAlias(functionAlias);
        return lambdaEventSourceBuildStepVariables;
    }

    public EventSourceConfig getEventSourceConfig() {
        return new EventSourceConfig(functionName, functionAlias, eventSourceArn);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion);
        } else {
            return new LambdaClientConfig(awsAccessKeyId, clearTextAwsSecretKey, awsRegion);
        }
    }

    private String expand(String value, EnvVars env) {
        return Util.replaceMacro(value.trim(), env);
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaEventSourceBuildStepVariables> {

        public String getDisplayName() {
            return "Add Event Source mapping";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LambdaEventSourceBuildStepVariables)) return false;

        LambdaEventSourceBuildStepVariables that = (LambdaEventSourceBuildStepVariables) o;

        if (useInstanceCredentials != that.useInstanceCredentials) return false;
        if (awsAccessKeyId != null ? !awsAccessKeyId.equals(that.awsAccessKeyId) : that.awsAccessKeyId != null)
            return false;
        if (awsSecretKey != null ? !awsSecretKey.equals(that.awsSecretKey) : that.awsSecretKey != null) return false;
        if (awsRegion != null ? !awsRegion.equals(that.awsRegion) : that.awsRegion != null) return false;
        if (functionName != null ? !functionName.equals(that.functionName) : that.functionName != null) return false;
        if (functionAlias != null ? !functionAlias.equals(that.functionAlias) : that.functionAlias != null)
            return false;
        return !(eventSourceArn != null ? !eventSourceArn.equals(that.eventSourceArn) : that.eventSourceArn != null);

    }

    @Override
    public int hashCode() {
        int result = (useInstanceCredentials ? 1 : 0);
        result = 31 * result + (awsAccessKeyId != null ? awsAccessKeyId.hashCode() : 0);
        result = 31 * result + (awsSecretKey != null ? awsSecretKey.hashCode() : 0);
        result = 31 * result + (awsRegion != null ? awsRegion.hashCode() : 0);
        result = 31 * result + (functionName != null ? functionName.hashCode() : 0);
        result = 31 * result + (functionAlias != null ? functionAlias.hashCode() : 0);
        result = 31 * result + (eventSourceArn != null ? eventSourceArn.hashCode() : 0);
        return result;
    }
}
