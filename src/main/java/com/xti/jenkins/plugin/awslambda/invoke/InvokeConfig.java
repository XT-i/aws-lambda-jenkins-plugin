package com.xti.jenkins.plugin.awslambda.invoke;

import java.io.Serializable;
import java.util.List;

public class InvokeConfig implements Serializable {
    private String functionName;
    private String qualifier;
    private String payload;
    private boolean synchronous;
    private List<JsonParameter> jsonParameters;

    public InvokeConfig(String functionName, final String qualifier, String payload, boolean synchronous, List<JsonParameter> jsonParameters) {
        this.functionName = functionName;
        this.qualifier = qualifier;
        this.payload = payload;
        this.synchronous = synchronous;
        this.jsonParameters = jsonParameters;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(final String qualifier) {
        this.qualifier = qualifier;
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

    public List<JsonParameter> getJsonParameters() {
        return jsonParameters;
    }

    public void setJsonParameters(List<JsonParameter> jsonParameters) {
        this.jsonParameters = jsonParameters;
    }
}
