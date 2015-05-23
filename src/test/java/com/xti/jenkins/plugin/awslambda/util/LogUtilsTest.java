package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.AmazonClientException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LogUtilsTest {

    @Test
    public void testGetStackTrace() throws Exception {
        try {
            throw new AmazonClientException("something wrong");
        } catch (AmazonClientException ace){
            String stackTrace = LogUtils.getStackTrace(ace);
            assertTrue(stackTrace.startsWith("com.amazonaws.AmazonClientException: something wrong"));
            assertTrue(stackTrace.contains("at com.xti.jenkins.plugin.awslambda.util.LogUtilsTest.testGetStackTrace"));     }
    }
}