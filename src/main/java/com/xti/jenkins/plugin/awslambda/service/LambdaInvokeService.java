package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.LogType;
import com.amazonaws.util.Base64;
import com.xti.jenkins.plugin.awslambda.exception.LambdaInvokeException;
import com.xti.jenkins.plugin.awslambda.invoke.InvokeConfig;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.Charset;

public class LambdaInvokeService {
    private AWSLambdaClient client;
    private JenkinsLogger logger;

    public LambdaInvokeService(AWSLambdaClient client, JenkinsLogger logger) {
        this.client = client;
        this.logger = logger;
    }

    /**
     * Synchronously or asynchronously invokes an AWS Lambda function.
     * If synchronously invoked, the AWS Lambda log is collected and the response payload is returned
     * @param invokeConfig AWS Lambda invocation configuration
     * @return response payload
     */
    public String invokeLambdaFunction(InvokeConfig invokeConfig) throws LambdaInvokeException {
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(invokeConfig.getFunctionName())
                .withPayload(invokeConfig.getPayload());

        if(invokeConfig.getQualifier() !=null && !invokeConfig.getQualifier().isEmpty()){
            invokeRequest.withQualifier(invokeConfig.getQualifier());
        }

        if(invokeConfig.isSynchronous()){
            invokeRequest
                    .withInvocationType(InvocationType.RequestResponse)
                    .withLogType(LogType.Tail);
        } else {
            invokeRequest
                    .withInvocationType(InvocationType.Event);
        }
        logger.log("Lambda invoke request:%n%s%nPayload:%n%s%n", invokeRequest.toString(), invokeConfig.getPayload());

        InvokeResult invokeResult = client.invoke(invokeRequest);
        String payload = "";
        if(invokeResult.getPayload() != null){
            payload = new String(invokeResult.getPayload().array(), Charset.forName("UTF-8"));
        }
        logger.log("Lambda invoke response:%n%s%nPayload:%n%s%n", invokeResult.toString(), payload);

        if(invokeResult.getLogResult() != null){
            logger.log("Log:%n%s%n", new String(Base64.decode(invokeResult.getLogResult()), Charset.forName("UTF-8")));
        }

        if(StringUtils.isNotEmpty(invokeResult.getFunctionError())){
            throw new LambdaInvokeException("Function returned error of type: " + invokeResult.getFunctionError());
        }

        return payload;
    }
}
