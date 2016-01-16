package com.xti.jenkins.plugin.awslambda.invoke;

import java.io.Serializable;
import java.util.List;

public class InvokeConfig implements Serializable {
    private String functionName;
    private String payload;
    private boolean synchronous;
    private List<JsonParameter> jsonParameters;

    public InvokeConfig(String functionName, String payload, boolean synchronous, List<JsonParameter> jsonParameters) {
        this.functionName = functionName;
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
