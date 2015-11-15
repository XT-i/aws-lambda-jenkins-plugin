package com.xti.jenkins.plugin.awslambda;

/*
 * #%L
 * AWS Lambda Upload Plugin
 * %%
 * Copyright (C) 2015 XT-i
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.xti.jenkins.plugin.awslambda.upload.AliasConfig;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.PublishConfig;
import com.xti.jenkins.plugin.awslambda.upload.UpdateModeValue;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Describable containing Lambda post build action config, checking feasibility of migrating it to upload package.
 */
public class LambdaVariables extends AbstractDescribableImpl<LambdaVariables> {
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private Secret awsSecretKey;
    private String awsRegion;
    private String artifactLocation;
    private String description;
    private String functionName;
    private String handler;
    private String memorySize;
    private String role;
    private String runtime;
    private String timeout;
    private boolean successOnly;
    private String updateMode;
    private boolean publishVersion;
    private String publishDescription;
    private boolean createAlias;
    private String aliasName;
    private String aliasDescription;

    @DataBoundConstructor
    public LambdaVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String artifactLocation, String description, String functionName, String handler, String memorySize, String role, String runtime, String timeout, boolean successOnly, String updateMode, boolean publishVersion, String publishDescription, boolean createAlias, String aliasName, String aliasDescription) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey;
        this.awsRegion = awsRegion;
        this.artifactLocation = artifactLocation;
        this.description = description;
        this.functionName = functionName;
        this.handler = handler;
        this.memorySize = memorySize;
        this.role = role;
        this.runtime = runtime;
        this.timeout = timeout;
        this.successOnly = successOnly;
        this.updateMode = updateMode;
        this.publishVersion = publishVersion;
        this.publishDescription = publishDescription;
        this.createAlias = createAlias;
        this.aliasName = aliasName;
        this.aliasDescription = aliasDescription;
    }

    public boolean getUseInstanceCredentials() {
        return useInstanceCredentials;
    }

    public void setUseInstanceCredentials(boolean useInstanceCredentials) {
        this.useInstanceCredentials = useInstanceCredentials;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public Secret getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(Secret awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public String getArtifactLocation() {
        return artifactLocation;
    }

    public void setArtifactLocation(String artifactLocation) {
        this.artifactLocation = artifactLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public boolean getSuccessOnly() {
        return successOnly;
    }

    public void setSuccessOnly(boolean successOnly) {
        this.successOnly = successOnly;
    }

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public boolean getPublishVersion() {
        return publishVersion;
    }

    public void setPublishVersion(boolean publishVersion) {
        this.publishVersion = publishVersion;
    }

    public String getPublishDescription() {
        return publishDescription;
    }

    public void setPublishDescription(String publishDescription) {
        this.publishDescription = publishDescription;
    }

    public boolean getCreateAlias() {
        return createAlias;
    }

    public void setCreateAlias(boolean createAlias) {
        this.createAlias = createAlias;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAliasDescription() {
        return aliasDescription;
    }

    public void setAliasDescription(String aliasDescription) {
        this.aliasDescription = aliasDescription;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        awsSecretKey = Secret.fromString(expand(Secret.toString(awsSecretKey), env));
        awsRegion = expand(awsRegion, env);
        artifactLocation = expand(artifactLocation, env);
        description = expand(description, env);
        functionName = expand(functionName, env);
        handler = expand(handler, env);
        role = expand(role, env);
        runtime = expand(runtime, env);
        memorySize = expand(memorySize, env);
        timeout = expand(timeout, env);
        publishDescription = expand(publishDescription, env);
        aliasName = expand(aliasName, env);
        aliasDescription = expand(aliasDescription, env);
    }

    public LambdaVariables getClone(){
        return new LambdaVariables(useInstanceCredentials, awsAccessKeyId, awsSecretKey, awsRegion, artifactLocation, description, functionName, handler, memorySize, role, runtime, timeout, successOnly, updateMode, publishVersion, publishDescription, createAlias, aliasName, aliasDescription);
    }

    public DeployConfig getUploadConfig(){
        return new DeployConfig(artifactLocation, description, functionName, handler, Integer.valueOf(memorySize), role, runtime, Integer.valueOf(timeout), updateMode);
    }

    public PublishConfig getPublishConfig(){
        return new PublishConfig(publishVersion, functionName, publishDescription);
    }

    public AliasConfig getAliasConfig(String functionVersion) {
        return new AliasConfig(createAlias, aliasName, aliasDescription, functionName, functionVersion);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion);
        } else {
            return new LambdaClientConfig(awsAccessKeyId, Secret.toString(awsSecretKey), awsRegion);
        }
    }

    private String expand(String value, EnvVars env) {
        if(value != null) {
            return Util.replaceMacro(value.trim(), env);
        } else {
            return null;
        }
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaVariables> {

        public FormValidation doCheckTimeout(@QueryParameter String value, @QueryParameter String updateMode) {
            UpdateModeValue updateModeValue = UpdateModeValue.fromString(updateMode);
            if(updateModeValue == UpdateModeValue.Full || updateModeValue == UpdateModeValue.Config) {

                try {
                    Integer.parseInt(value);
                    return FormValidation.ok();
                } catch (NumberFormatException e) {
                    return FormValidation.warning("Not a number, might evaluate to number as environment variable.");
                }
            } else {
                return FormValidation.ok();
            }
        }

        public FormValidation doCheckMemorySize(@QueryParameter String value, @QueryParameter String updateMode) {
            UpdateModeValue updateModeValue = UpdateModeValue.fromString(updateMode);
            if(updateModeValue == UpdateModeValue.Full || updateModeValue == UpdateModeValue.Config) {
                try {
                    Integer.parseInt(value);
                    return FormValidation.ok();
                } catch (NumberFormatException e) {
                    return FormValidation.warning("Not a number, might evaluate to number as environment variable.");
                }
            } else {
                return FormValidation.ok();
            }
        }

        public ListBoxModel doFillUpdateModeItems(@QueryParameter String updateMode) {
            ListBoxModel items = new ListBoxModel();
            for (UpdateModeValue updateModeValue : UpdateModeValue.values()) {
                items.add(new ListBoxModel.Option(updateModeValue.getDisplayName(), updateModeValue.getMode(), updateModeValue.getMode().equals(updateMode)));
            }
            return items;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Deploy into Lambda";
        }

    }


}
