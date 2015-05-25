package com.xti.jenkins.plugin.awslambda.exception;

public class LambdaInvokeException extends AWSLambdaPluginException {
    public LambdaInvokeException(String message) {
        super(message);
    }
}
