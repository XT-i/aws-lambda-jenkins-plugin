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

import java.util.ArrayList;
import java.util.List;

public class LambdaInvokeVariables extends AbstractDescribableImpl<LambdaInvokeVariables> {
    private String awsAccessKeyId;
    private Secret awsSecretKey;
    private String awsRegion;
    private String functionName;
    private String payload;
    private boolean synchronous;
    private boolean successOnly;
    private List<JsonParameterVariables> jsonParameters = new ArrayList<JsonParameterVariables>();

    @DataBoundConstructor
    public LambdaInvokeVariables(String awsAccessKeyId, Secret awsSecretKey, String awsRegion, String functionName, String payload, boolean synchronous, boolean successOnly, List<JsonParameterVariables> jsonParameters) {
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretKey = awsSecretKey;
        this.awsRegion = awsRegion;
        this.functionName = functionName;
        this.payload = payload;
        this.synchronous = synchronous;
        this.successOnly = successOnly;
        this.jsonParameters = jsonParameters;
    }

    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public Secret getAwsSecretKey() {
        return awsSecretKey;
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

    public boolean getSynchronous(){
        return synchronous;
    }

    public boolean getSuccessOnly() {
        return successOnly;
    }

    public List<JsonParameterVariables> getJsonParameters() {
        if(jsonParameters == null){
            return new ArrayList<JsonParameterVariables>();
        } else {
            return jsonParameters;
        }
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public void setAwsSecretKey(Secret awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void setSuccessOnly(boolean successOnly) {
        this.successOnly = successOnly;
    }

    public void setJsonParameters(List<JsonParameterVariables> jsonParameters) {
        this.jsonParameters = jsonParameters;
    }

    public void expandVariables(EnvVars env) {
        awsAccessKeyId = expand(awsAccessKeyId, env);
        awsSecretKey = Secret.fromString(expand(Secret.toString(awsSecretKey), env));
        awsRegion = expand(awsRegion, env);
        functionName = expand(functionName, env);
    }

    public LambdaInvokeVariables getClone(){
        return new LambdaInvokeVariables(awsAccessKeyId, awsSecretKey, awsRegion, functionName, payload, synchronous, successOnly, jsonParameters);
    }

    private String expand(String value, EnvVars env) {
        return Util.replaceMacro(value.trim(), env);
    }

    public InvokeConfig getInvokeConfig(){
        List<JsonParameter> jsonParameters = new ArrayList<JsonParameter>();
        for (JsonParameterVariables jsonParameterVariables : getJsonParameters()) {
            jsonParameters.add(jsonParameterVariables.getJsonParameter());
        }
        return new InvokeConfig(functionName, payload, synchronous, jsonParameters);
    }

    public LambdaClientConfig getLambdaClientConfig(){
        return new LambdaClientConfig(awsAccessKeyId, Secret.toString(awsSecretKey), awsRegion);
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends AWSLambdaDescriptor<LambdaInvokeVariables> {

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

        LambdaInvokeVariables that = (LambdaInvokeVariables) o;

        if (synchronous != that.synchronous) return false;
        if (successOnly != that.successOnly) return false;
        if (awsAccessKeyId != null ? !awsAccessKeyId.equals(that.awsAccessKeyId) : that.awsAccessKeyId != null)
            return false;
        if (awsSecretKey != null ? !awsSecretKey.equals(that.awsSecretKey) : that.awsSecretKey != null) return false;
        if (awsRegion != null ? !awsRegion.equals(that.awsRegion) : that.awsRegion != null) return false;
        if (functionName != null ? !functionName.equals(that.functionName) : that.functionName != null) return false;
        if (payload != null ? !payload.equals(that.payload) : that.payload != null) return false;
        return !(jsonParameters != null ? !jsonParameters.equals(that.jsonParameters) : that.jsonParameters != null);

    }

    @Override
    public int hashCode() {
        int result = awsAccessKeyId != null ? awsAccessKeyId.hashCode() : 0;
        result = 31 * result + (awsSecretKey != null ? awsSecretKey.hashCode() : 0);
        result = 31 * result + (awsRegion != null ? awsRegion.hashCode() : 0);
        result = 31 * result + (functionName != null ? functionName.hashCode() : 0);
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        result = 31 * result + (synchronous ? 1 : 0);
        result = 31 * result + (successOnly ? 1 : 0);
        result = 31 * result + (jsonParameters != null ? jsonParameters.hashCode() : 0);
        return result;
    }
}
