package com.xti.jenkins.plugin.awslambda.upload;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DeployConfig implements Serializable {
    private String artifactLocation;
    private String description;
    private String functionName;
    private String handler;
    private Integer memorySize;
    private Boolean publish;
    private String role;
    private String runtime;
    private Integer timeout;
    private String updateMode;
    private String alias;
    private Boolean createAlias;
    private List<String> subnets;
    private List<String> securityGroups;
    private Map<String, String> environmentVariables;
    private String kmsArn;

    public DeployConfig(String artifactLocation, String description, String functionName, String handler, Integer memorySize, String role, String runtime, Integer timeout, String updateMode, boolean publish, String alias, boolean createAlias, List<String> subnets, List<String> securityGroups) {
        this.artifactLocation = artifactLocation;
        this.description = description;
        this.functionName = functionName;
        this.handler = handler;
        this.memorySize = memorySize;
        this.role = role;
        this.runtime = runtime;
        this.timeout = timeout;
        this.updateMode = updateMode;
        this.publish = publish;
        this.alias = alias;
        this.createAlias = createAlias;
        this.subnets = subnets;
        this.securityGroups = securityGroups;
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

    public Boolean getPublish() { return publish; }

    public void setPublish(Boolean publish) { this.publish = publish; }

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

    public String getUpdateMode() {
        return updateMode;
    }

    public void setUpdateMode(String updateMode) {
        this.updateMode = updateMode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getCreateAlias() {
        return createAlias;
    }

    public void setCreateAlias(Boolean createAlias) {
        this.createAlias = createAlias;
    }

    public List<String> getSubnets() {
        return subnets;
    }

    public void setSubnets(List<String> subnets) {
        this.subnets = subnets;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getKmsArn() {
        return kmsArn;
    }

    public void setKmsArn(String kmsArn) {
        this.kmsArn = kmsArn;
    }
}
