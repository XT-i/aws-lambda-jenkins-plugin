package com.xti.jenkins.plugin.awslambda.exception;

public class LambdaInvokeException extends RuntimeException {
    public LambdaInvokeException(String message) {
        super(message);
    }
}
