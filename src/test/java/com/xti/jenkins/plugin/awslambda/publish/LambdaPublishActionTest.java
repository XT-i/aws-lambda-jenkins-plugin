package com.xti.jenkins.plugin.awslambda.publish;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Magnus Sulland on 4/08/2016.
 */

public class LambdaPublishActionTest {

    @Test
    public void testLambdaUploadActionSuccess(){
        LambdaPublishAction lambdaUploadAction = new LambdaPublishAction("42", "alias1", true);

        assertEquals("Lambda alias alias1 points to version 42", lambdaUploadAction.getDisplayName());
        assertEquals("console", lambdaUploadAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24.png", lambdaUploadAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48.png", lambdaUploadAction.getBigIconFileName());
    }

    @Test
    public void testLambdaUploadActionFailure(){
        LambdaPublishAction lambdaUploadAction = new LambdaPublishAction("42", "alias1", false);

        assertEquals("Unable to point alias alias1 to version 42", lambdaUploadAction.getDisplayName());
        assertEquals("console", lambdaUploadAction.getUrlName());
        assertEquals("/plugin/aws-lambda/images/Lambda_24_grey.png", lambdaUploadAction.getIconFileName());
        assertEquals("/plugin/aws-lambda/images/Lambda_48_grey.png", lambdaUploadAction.getBigIconFileName());
    }

}