package com.xti.jenkins.plugin.awslambda.invoke;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LambdaInvokeActionTest {

    @Test
    public void testLambdaInvokeActionSuccess(){
        LambdaInvokeAction lambdaInvokeAction = new LambdaInvokeAction("lambda1", null, true);

        assertEquals("Invoked Lambda: lambda1", lambdaInvokeAction.getDisplayName());
        assertEquals("console", lambdaInvokeAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24.png", lambdaInvokeAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48.png", lambdaInvokeAction.getBigIconFileName());
    }

    @Test
    public void testLambdaInvokeActionFailure(){
        LambdaInvokeAction lambdaInvokeAction = new LambdaInvokeAction("lambda1",null, false);

        assertEquals("Lambda invocation failure: lambda1", lambdaInvokeAction.getDisplayName());
        assertEquals("console", lambdaInvokeAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24_grey.png", lambdaInvokeAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48_grey.png", lambdaInvokeAction.getBigIconFileName());
    }

    @Test
    public void testLambdaInvokeActionSuccessWithQualifier(){
        LambdaInvokeAction lambdaInvokeAction = new LambdaInvokeAction("lambda1", "alias", true);

        assertEquals("Invoked Lambda: lambda1:alias", lambdaInvokeAction.getDisplayName());
        assertEquals("console", lambdaInvokeAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24.png", lambdaInvokeAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48.png", lambdaInvokeAction.getBigIconFileName());
    }

    @Test
    public void testLambdaInvokeActionFailureWithQualifier(){
        LambdaInvokeAction lambdaInvokeAction = new LambdaInvokeAction("lambda1","alias", false);

        assertEquals("Lambda invocation failure: lambda1:alias", lambdaInvokeAction.getDisplayName());
        assertEquals("console", lambdaInvokeAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24_grey.png", lambdaInvokeAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48_grey.png", lambdaInvokeAction.getBigIconFileName());
    }
}