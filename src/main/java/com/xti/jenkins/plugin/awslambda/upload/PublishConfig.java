package com.xti.jenkins.plugin.awslambda.upload;

public class PublishConfig {
    private boolean publishVersion;
    private String functionName;
    private String publishDescription;

    public PublishConfig(boolean publishVersion, String functionName, String publishDescription) {
        this.publishVersion = publishVersion;
        this.functionName = functionName;
        this.publishDescription = publishDescription;
    }

    public boolean isPublishVersion() {
        return publishVersion;
    }

    public void setPublishVersion(boolean publishVersion) {
        this.publishVersion = publishVersion;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPublishDescription() {
        return publishDescription;
    }

    public void setPublishDescription(String publishDescription) {
        this.publishDescription = publishDescription;
    }
}
