package com.xti.jenkins.plugin.awslambda.upload;

public class DeployConfig {
    private String awsAccessKeyId;
    private String awsSecretKey;
    private String awsRegion;
    private String artifactLocation;
    private String description;
    private String functionName;
    private String handler;
    private Integer memorySize;
    private String role;
    private String runtime;
    private Integer timeout;
    private boolean successOnly;
    private String updateMode;

    public DeployConfig(String awsAccessKeyId, String awsSecretKey, String awsRegion, String artifactLocation, String description, String functionName, String handler, Integer memorySize, String role, String runtime, Integer timeout, boolean successOnly, String updateMode) {
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

    public Integer getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(Integer memorySize) {
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

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean isSuccessOnly() {
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
}
