package com.xti.jenkins.plugin.awslambda.publish;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class PublishConfig {
    private String functionAlias;
    private String functionARN;
    private String versionDescription;

    public PublishConfig(String functionAlias, String functionARN, String versionDescription){
        this.functionAlias = functionAlias;
        this.functionARN = functionARN;
        this.versionDescription = versionDescription;
    }

    public void setFunctionAlias(String alias) {
        this.functionAlias = alias;
    }

    public String getFunctionAlias(){
        return this.functionAlias;
    }

    public void setFunctionARN(String functionARN){
        this.functionARN = functionARN;
    }

    public String getFunctionARN(){
        return this.functionARN;
    }

    public String getVersionDescription() {
        return this.versionDescription;
    }

    public void setVersionDescription(String versionDescription){
        this.versionDescription = versionDescription;
    }
}
