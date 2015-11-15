package com.xti.jenkins.plugin.awslambda.upload;

public class AliasConfig {
    public AliasConfig(boolean createAlias, String aliasName, String aliasDescription, String functionName, String functionVersion) {
        this.createAlias = createAlias;
        this.aliasName = aliasName;
        this.aliasDescription = aliasDescription;
        this.functionName = functionName;
        this.functionVersion = functionVersion;
    }

    private boolean createAlias;
    private String aliasName;
    private String aliasDescription;
    private String functionName;
    private String functionVersion;

    public boolean isCreateAlias() {
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
