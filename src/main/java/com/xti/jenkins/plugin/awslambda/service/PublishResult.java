package com.xti.jenkins.plugin.awslambda.service;

public class PublishResult {
    private boolean success;
    private String functionName;
    private String functionVersion;

    public PublishResult(boolean success, String functionName, String functionVersion) {
        this.success = success;
        this.functionName = functionName;
        this.functionVersion = functionVersion;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionVersion() {
        return functionVersion;
    }

    public void setFunctionVersion(String functionVersion) {
        this.functionVersion = functionVersion;
    }
}
