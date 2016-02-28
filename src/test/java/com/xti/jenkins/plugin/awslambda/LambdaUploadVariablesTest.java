package com.xti.jenkins.plugin.awslambda;

import com.amazonaws.services.lambda.AWSLambda;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadVariables;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.util.Secret;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LambdaUploadVariablesTest {

    @Test
    public void testCloneExpandVariables() throws Exception {
        LambdaUploadVariables variables = new LambdaUploadVariables(false, "${ENV_ID}", Secret.fromString("$ENV_SECRET}"), "${ENV_REGION}", "${ENV_FILE}", "description ${ENV_DESCRIPTION}", "${ENV_FUNCTION}", "${ENV_HANDLER}", "${ENV_MEMORY_SIZE}", "${ENV_ROLE}", "$ENV_RUNTIME", "${ENV_TIMEOUT}", true, false, "full", null, false, "", "");
        LambdaUploadVariables clone = variables.getClone();

        EnvVars envVars = new EnvVars();
        envVars.put("ENV_ID", "ID");
        envVars.put("ENV_SECRET", "SECRET");
        envVars.put("ENV_REGION", "eu-west-1");
        envVars.put("ENV_FILE", "FILE");
        envVars.put("ENV_DESCRIPTION", "DESCRIPTION");
        envVars.put("ENV_FUNCTION", "FUNCTION");
        envVars.put("ENV_HANDLER", "HANDLER");
        envVars.put("ENV_ROLE", "ROLE");
        envVars.put("ENV_RUNTIME", "RUNTIME");
        envVars.put("ENV_MEMORY_SIZE", "1024");
        envVars.put("ENV_TIMEOUT", "30");
        clone.expandVariables(envVars);

        LambdaUploadVariables expected = new LambdaUploadVariables(false, "ID", Secret.fromString("SECRET}"), "eu-west-1", "FILE", "description DESCRIPTION", "FUNCTION", "HANDLER", "1024", "ROLE", "RUNTIME", "30", true, false, "full", null, false, "", "");

        assertEquals(expected.getAwsAccessKeyId(), clone.getAwsAccessKeyId());
        assertEquals(expected.getAwsSecretKey(), clone.getAwsSecretKey());
        assertEquals(expected.getAwsRegion(), clone.getAwsRegion());
        assertEquals(expected.getArtifactLocation(), clone.getArtifactLocation());
        assertEquals(expected.getDescription(), clone.getDescription());
        assertEquals(expected.getFunctionName(), clone.getFunctionName());
        assertEquals(expected.getMemorySize(), clone.getMemorySize());
        assertEquals(expected.getTimeout(), clone.getTimeout());
        assertEquals(expected.getHandler(), clone.getHandler());
        assertEquals(expected.getRole(), clone.getRole());
        assertEquals(expected.getRuntime(), clone.getRuntime());
        assertEquals(expected.getSuccessOnly(), clone.getSuccessOnly());
    }

    @Test
    public void testGetUploadConfig() throws Exception {
        LambdaUploadVariables variables = new LambdaUploadVariables(false, "ID", Secret.fromString("SECRET}"), "eu-west-1", "FILE", "description DESCRIPTION", "FUNCTION", "HANDLER", "1024", "ROLE", "RUNTIME", "30", true, false, "full", null, false, "subnet1, subnet2", "secgroup");
        DeployConfig uploadConfig = variables.getUploadConfig();

        assertEquals(variables.getArtifactLocation(), uploadConfig.getArtifactLocation());
        assertEquals(variables.getDescription(), uploadConfig.getDescription());
        assertEquals(Integer.valueOf(variables.getMemorySize()), uploadConfig.getMemorySize());
        assertEquals(Integer.valueOf(variables.getTimeout()), uploadConfig.getTimeout());
        assertEquals(variables.getHandler(), uploadConfig.getHandler());
        assertEquals(variables.getRuntime(), uploadConfig.getRuntime());
        assertEquals(variables.getFunctionName(), uploadConfig.getFunctionName());
        assertEquals(variables.getRole(), uploadConfig.getRole());
        assertEquals(variables.getUpdateMode(), uploadConfig.getUpdateMode());
        assertEquals(Arrays.asList("subnet1", "subnet2"), uploadConfig.getSubnets());
        assertEquals(Collections.singletonList("secgroup"), uploadConfig.getSecurityGroups());
    }

    @Test
    public void testGetLambdaClientConfig() throws Exception {
        LambdaUploadVariables variables = new LambdaUploadVariables(false, "ID", Secret.fromString("SECRET}"), "eu-west-1", "FILE", "description DESCRIPTION", "FUNCTION", "HANDLER", "1024", "ROLE", "RUNTIME", "30", true, false, "full", null, false, "subnet1, subnet2", "secgroup");
        LambdaClientConfig lambdaClientConfig = variables.getLambdaClientConfig();

        AWSLambda lambda = lambdaClientConfig.getClient();
        assertNotNull(lambda);
    }
}