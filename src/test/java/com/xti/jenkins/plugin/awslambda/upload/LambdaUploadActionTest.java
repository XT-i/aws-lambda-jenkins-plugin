package com.xti.jenkins.plugin.awslambda.upload;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LambdaUploadActionTest {

    @Test
    public void testLambdaUploadActionSuccess(){
        LambdaUploadAction lambdaUploadAction = new LambdaUploadAction("lambda1", true);

        assertEquals("Deployed Lambda: lambda1", lambdaUploadAction.getDisplayName());
        assertEquals("console", lambdaUploadAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24.png", lambdaUploadAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48.png", lambdaUploadAction.getBigIconFileName());
    }

    @Test
    public void testLambdaUploadActionFailure(){
        LambdaUploadAction lambdaUploadAction = new LambdaUploadAction("lambda1", false);

        assertEquals("Lambda deployment failure: lambda1", lambdaUploadAction.getDisplayName());
        assertEquals("console", lambdaUploadAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24_grey.png", lambdaUploadAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48_grey.png", lambdaUploadAction.getBigIconFileName());
    }

}