package com.xti.jenkins.plugin.awslambda.invoke;

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

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.ArrayList;
import java.util.List;

public class LambdaInvokeBuildStepVariables extends AbstractDescribableImpl<LambdaInvokeBuildStepVariables> {
    private boolean useInstanceCredentials;
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String clearTextAwsSecretKey;
    private String awsRegion;
    private String functionName;
    private String payload;
    private boolean synchronous;
    private List<JsonParameterVariables> jsonParameters;

    @DataBoundConstructor
    public LambdaInvokeBuildStepVariables(String awsRegion, String functionName, boolean synchronous) {
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.synchronous = synchronous;
    }

    @Deprecated
    public LambdaInvokeBuildStepVariables(boolean useInstanceCredentials, String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String payload, boolean synchronous, List<JsonParameterVariables> jsonParameters) {
        this.useInstanceCredentials = useInstanceCredentials;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey != null ? awsSecretKey.getEncryptedValue() : null;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.payload = payload;
        this.synchronous = synchronous;
        this.jsonParameters = jsonParameters;
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

    public String getFunctionName() {
        return functionName;
    }

    public String getPayload() {
        return payload;
    }

    @DataBoundSetter
    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean getSynchronous() {
        return synchronous;
    }

    public List<JsonParameterVariables> getJsonParameters() {
        if(jsonParameters == null){
            return new ArrayList<>();
        } else {
            return jsonParameters;
        }
    }

    @DataBoundSetter
    public void setJsonParameters(List<JsonParameterVariables> jsonParameters) {
        this.jsonParameters = jsonParameters;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        clearTextAwsSecretKey = expand(Secret.toString(Secret.fromString(awsSecretKey)), env);
        awsRegion = expand(awsRegion, env);
        functionName = expand(functionName, env);
        payload = expand(payload, env);
        if(jsonParameters != null) {
            for (JsonParameterVariables jsonParameter : jsonParameters) {
                jsonParameter.expandVariables(env);
            }
        }
    }

    public LambdaInvokeBuildStepVariables getClone(){
        LambdaInvokeBuildStepVariables lambdaInvokeBuildStepVariables = new LambdaInvokeBuildStepVariables(awsRegion, functionName, synchronous);
        lambdaInvokeBuildStepVariables.setUseInstanceCredentials(useInstanceCredentials);
        lambdaInvokeBuildStepVariables.setAwsAccessKeyId(awsAccessKeyId);
        lambdaInvokeBuildStepVariables.setAwsSecretKey(awsSecretKey);
        lambdaInvokeBuildStepVariables.setPayload(payload);
        lambdaInvokeBuildStepVariables.setJsonParameters(jsonParameters);
        return lambdaInvokeBuildStepVariables;
    }

    private String expand(String value, EnvVars env) {
        return Util.replaceMacro(value.trim(), env);
    }

    public InvokeConfig getInvokeConfig(){
        List<JsonParameter> jsonParameters = new ArrayList<>();
        for (JsonParameterVariables jsonParameterVariables : getJsonParameters()) {
            jsonParameters.add(jsonParameterVariables.buildJsonParameter());
        }
        return new InvokeConfig(functionName, payload, synchronous, jsonParameters);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        if(useInstanceCredentials){
            return new LambdaClientConfig(awsRegion);
        } else {
            return new LambdaClientConfig(awsAccessKeyId, clearTextAwsSecretKey, awsRegion);
        }
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaInvokeBuildStepVariables> {

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Invoke Lambda function";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LambdaInvokeBuildStepVariables variables = (LambdaInvokeBuildStepVariables) o;

        if (getUseInstanceCredentials() != variables.getUseInstanceCredentials()) return false;
        if (getSynchronous() != variables.getSynchronous()) return false;
        if (getAwsAccessKeyId() != null ? !getAwsAccessKeyId().equals(variables.getAwsAccessKeyId()) : variables.getAwsAccessKeyId() != null)
            return false;
        if (getAwsSecretKey() != null ? !getAwsSecretKey().equals(variables.getAwsSecretKey()) : variables.getAwsSecretKey() != null)
            return false;
        if (getAwsRegion() != null ? !getAwsRegion().equals(variables.getAwsRegion()) : variables.getAwsRegion() != null)
            return false;
        if (getFunctionName() != null ? !getFunctionName().equals(variables.getFunctionName()) : variables.getFunctionName() != null)
            return false;
        if (getPayload() != null ? !getPayload().equals(variables.getPayload()) : variables.getPayload() != null)
            return false;
        return !(getJsonParameters() != null ? !getJsonParameters().equals(variables.getJsonParameters()) : variables.getJsonParameters() != null);

    }

    @Override
    public int hashCode() {
        int result = (getUseInstanceCredentials() ? 1 : 0);
        result = 31 * result + (getAwsAccessKeyId() != null ? getAwsAccessKeyId().hashCode() : 0);
        result = 31 * result + (getAwsSecretKey() != null ? getAwsSecretKey().hashCode() : 0);
        result = 31 * result + (getAwsRegion() != null ? getAwsRegion().hashCode() : 0);
        result = 31 * result + (getFunctionName() != null ? getFunctionName().hashCode() : 0);
        result = 31 * result + (getPayload() != null ? getPayload().hashCode() : 0);
        result = 31 * result + (getSynchronous() ? 1 : 0);
        result = 31 * result + (getJsonParameters() != null ? getJsonParameters().hashCode() : 0);
        return result;
    }
}
