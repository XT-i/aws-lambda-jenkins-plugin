package com.xti.jenkins.plugin.awslambda.publish;

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
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishBuildStepVariables extends AbstractDescribableImpl<LambdaPublishBuildStepVariables> {

    private boolean useInstanceCredentials;

    private String awsRegion;
    private String functionARN;
    private String functionAlias;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String clearTextAwsSecretKey;
    private String versionDescription;

    @DataBoundConstructor
    public LambdaPublishBuildStepVariables(String awsRegion, String functionARN, String functionAlias, String versionDescription){
        this.awsRegion = awsRegion;
        this.functionARN = functionARN;
        this.functionAlias = functionAlias;
        this.versionDescription = versionDescription;
    }

    @Deprecated
    public LambdaPublishBuildStepVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionARN, String functionAlias, String versionDescription) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey != null ? awsSecretKey.getEncryptedValue() : null;
        this.awsRegion = awsRegion;
        this.functionARN = functionARN;
        this.functionAlias = functionAlias;
        this.versionDescription = versionDescription;
    }

    @DataBoundSetter
    public void setUseInstanceCredentials(boolean useInstanceCredentials){
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsAccessKeyId() {
        return this.awsAccessKeyId;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = Secret.fromString(awsSecretKey).getEncryptedValue();
    }

    public String getAwsSecretKey() {
        return this.awsSecretKey;
    }

    public String getAwsRegion() {
        return this.awsRegion;
    }

    public String getFunctionARN() {
        return this.functionARN;
    }

    public String getFunctionAlias() {
        return this.functionAlias;
    }

    public String getVersionDescription() {
        return this.versionDescription;
    }

    public void setVersionDescription(String versionDescription){
        this.versionDescription = versionDescription;
    }

    public PublishConfig getPublishConfig() {
        return new PublishConfig(this.functionAlias, this.functionARN, this.versionDescription);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion, JenkinsProxy.getConfig());
        } else {
            return new LambdaClientConfig(awsAccessKeyId, clearTextAwsSecretKey, awsRegion, JenkinsProxy.getConfig());
        }
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = ExpansionUtils.expand(awsAccessKeyId, env);
        clearTextAwsSecretKey = ExpansionUtils.expand(Secret.toString(Secret.fromString(this.awsSecretKey)), env);
        awsRegion = ExpansionUtils.expand(this.awsRegion, env);
        functionARN = ExpansionUtils.expand(this.functionARN, env);
        functionAlias = ExpansionUtils.expand(this.functionAlias, env);
        versionDescription = ExpansionUtils.expand(this.versionDescription, env);
    }

    public LambdaPublishBuildStepVariables getClone(){
        LambdaPublishBuildStepVariables lambdaPublishBuildStepVariables = new LambdaPublishBuildStepVariables(this.awsRegion, this.functionARN, this.functionAlias, this.versionDescription);
        lambdaPublishBuildStepVariables.setAwsAccessKeyId(this.awsAccessKeyId);
        lambdaPublishBuildStepVariables.setAwsSecretKey(this.awsSecretKey);
        lambdaPublishBuildStepVariables.setUseInstanceCredentials(this.useInstanceCredentials);
        return lambdaPublishBuildStepVariables;
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaPublishBuildStepVariables> {
        public String getDisplayName() {
            return "AWS Lambda publish new version and update alias";
        }
    }

}