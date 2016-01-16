package com.xti.jenkins.plugin.awslambda.service;

import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class LambdaClientConfigTest {

    @Test
    public void testGetClient() throws Exception {
        LambdaClientConfig lambdaClientConfig = new LambdaClientConfig("abc", "def", "eu-west-1");
        assertNotNull(lambdaClientConfig.getClient());
    }

    @Test
    public void testGetClientInvalidRegion() throws Exception {
        try {
            LambdaClientConfig lambdaClientConfig = new LambdaClientConfig("abc", "def", "ghi");
            lambdaClientConfig.getClient();
            fail("Should have failed with IllegalArgumentException");
        } catch (IllegalArgumentException ignored){
        }
    }
}