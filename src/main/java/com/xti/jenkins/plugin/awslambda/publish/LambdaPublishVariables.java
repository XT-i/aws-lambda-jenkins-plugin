package com.xti.jenkins.plugin.awslambda.publish;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.util.ExpansionUtils;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishVariables  extends AbstractDescribableImpl<LambdaPublishVariables> {

    private boolean useInstanceCredentials;

    private String awsRegion;
    private String functionARN;
    private String functionAlias;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String clearTextAwsSecretKey;
    private String versionDescription;

    @DataBoundConstructor
    public LambdaPublishVariables(String awsRegion, String functionARN, String functionAlias, String versionDescription){
        this.awsRegion = awsRegion;
        this.functionARN = functionARN;
        this.functionAlias = functionAlias;
        this.versionDescription = versionDescription;
    }

    @Deprecated
    public LambdaPublishVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionARN, String functionAlias, String versionDescription
    ) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey != null ? awsSecretKey.getEncryptedValue() : null;
        this.awsRegion = awsRegion;
        this.functionARN = functionARN;
        this.functionAlias = functionAlias;
        this.versionDescription = versionDescription;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    public void setUseInstanceCredentials(boolean useInstanceCredentials){
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public String getAwsAccessKeyId() {
        return this.awsAccessKeyId;
    }

    @DataBoundSetter
    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return this.awsSecretKey;
    }

    @DataBoundSetter
    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = Secret.fromString(awsSecretKey).getEncryptedValue();
    }

    public String getAwsRegion() {
        return this.awsRegion;
    }

    @DataBoundSetter
    public void setFunctionARN(String functionARN){
        this.functionARN = functionARN;
    }

    public String getFunctionARN() {
        return this.functionARN;
    }

    @DataBoundSetter
    public void setFunctionAlias(String functionAlias){
        this.functionAlias = functionAlias;
    }

    public String getFunctionAlias() {
        return this.functionAlias;
    }

    public void setVersionDescription(String versionDescription){
        this.versionDescription = versionDescription;
    }

    public String getVersionDescription(){
        return this.versionDescription;
    }

    public PublishConfig getPublishConfig() {
        return new PublishConfig(this.functionAlias, this.functionARN, this.versionDescription);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion);
        } else {
            return new LambdaClientConfig(awsAccessKeyId, clearTextAwsSecretKey, awsRegion);
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

    public LambdaPublishVariables getClone(){
        LambdaPublishVariables lambdaPublishVariables = new LambdaPublishVariables(this.awsRegion, this.functionARN, this.functionAlias, this.versionDescription);
        lambdaPublishVariables.setAwsAccessKeyId(this.awsAccessKeyId);
        lambdaPublishVariables.setAwsSecretKey(this.awsSecretKey);
        lambdaPublishVariables.setUseInstanceCredentials(this.useInstanceCredentials);
        return lambdaPublishVariables;
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaPublishVariables> {

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Lambda publish and change alias";
        }

    }
}