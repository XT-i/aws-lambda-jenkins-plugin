package com.xti.jenkins.plugin.awslambda.invoke;

import java.util.List;

public class InvokeConfig {
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String awsRegion;
    private String functionName;
    private String payload;
    private boolean synchronous;
    private boolean successOnly;
    private List<JsonParameter> jsonParameters;

    public InvokeConfig(String awsAccessKeyId, String awsSecretKey, String awsRegion, String functionName, String payload, boolean synchronous, boolean successOnly, List<JsonParameter> jsonParameters) {
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

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public void setAwsSecretKey(String awsSecretKey) {
        this.awsSecretKey = awsSecretKey;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public boolean isSuccessOnly() {
        return successOnly;
    }

    public void setSuccessOnly(boolean successOnly) {
        this.successOnly = successOnly;
    }

    public List<JsonParameter> getJsonParameters() {
        return jsonParameters;
    }

    public void setJsonParameters(List<JsonParameter> jsonParameters) {
        this.jsonParameters = jsonParameters;
    }
}
