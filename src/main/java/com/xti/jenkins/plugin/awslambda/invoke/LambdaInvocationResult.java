package com.xti.jenkins.plugin.awslambda.invoke;

import java.io.Serializable;
import java.util.Map;

public class LambdaInvocationResult implements Serializable {
    private boolean success;
    private Map<String, String> injectables;

    public LambdaInvocationResult(boolean success, Map<String, String> injectables) {
        this.success = success;
        this.injectables = injectables;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, String> getInjectables() {
        return injectables;
    }
}
