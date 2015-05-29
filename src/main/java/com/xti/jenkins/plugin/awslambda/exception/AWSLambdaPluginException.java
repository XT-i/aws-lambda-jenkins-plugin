package com.xti.jenkins.plugin.awslambda.exception;

public class AWSLambdaPluginException extends RuntimeException {
    public AWSLambdaPluginException(String message) {
        super(message);
    }

    public AWSLambdaPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
