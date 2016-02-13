package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by anthonyikeda on 25/11/2015.
 *
 */
public class LambdaEventSourceBuildStepVariables extends AbstractDescribableImpl<LambdaEventSourceBuildStepVariables>{
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private Secret awsSecretKey;
    private String awsRegion;
    private String functionName;
    private String functionAlias;
    private String eventSourceArn;


    @DataBoundConstructor
    public LambdaEventSourceBuildStepVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String functionAlias, String eventSourceArn) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.functionAlias = functionAlias;
        this.eventSourceArn = eventSourceArn;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public Secret getAwsSecretKey() {
        return awsSecretKey;
    }

    public String getFunctionName() {
        return functionName;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    public String getEventSourceArn() {
        return eventSourceArn;
    }

    public String getFunctionAlias() {
        return functionAlias;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public void setAwsSecretKey(Secret awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setUseInstanceCredentials(boolean useInstanceCredentials) {
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public void setEventSourceArn(String eventSourceArn) {
        this.eventSourceArn = eventSourceArn;
    }

    public void setFunctionAlias(String functionAlias) {
        this.functionAlias = functionAlias;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        awsSecretKey = Secret.fromString(expand(Secret.toString(awsSecretKey), env));
        awsRegion = expand(awsRegion, env);
        functionName = expand(functionName, env);
        functionAlias = expand(functionAlias, env);
        eventSourceArn = expand(eventSourceArn, env);
    }

    public LambdaEventSourceBuildStepVariables getClone() {
        return new LambdaEventSourceBuildStepVariables(useInstanceCredentials, awsAccessKeyId, awsSecretKey, awsRegion, functionName, functionAlias, eventSourceArn);
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

    private String expand(String value, EnvVars env) {
        return Util.replaceMacro(value.trim(), env);
    }

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
