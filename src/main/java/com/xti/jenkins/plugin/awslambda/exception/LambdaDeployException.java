package com.xti.jenkins.plugin.awslambda.exception;

public class LambdaDeployException extends AWSLambdaPluginException {

    public LambdaDeployException(String message) {
        super(message);
    }

    public LambdaDeployException(String message, Throwable cause) {
        super(message, cause);
    }
}
