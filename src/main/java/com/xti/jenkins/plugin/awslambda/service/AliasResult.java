package com.xti.jenkins.plugin.awslambda.service;

public class AliasResult {
    private boolean success;
    private String functionName;
    private String functionVersion;
    private String functionAlias;

    public AliasResult(boolean success, String functionName, String functionVersion, String functionAlias) {
        this.success = success;
        this.functionName = functionName;
        this.functionVersion = functionVersion;
        this.functionAlias = functionAlias;
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

    public String getFunctionAlias() {
        return functionAlias;
    }

    public void setFunctionAlias(String functionAlias) {
        this.functionAlias = functionAlias;
    }
}
