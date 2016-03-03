package com.xti.jenkins.plugin.awslambda.invoke;

import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LambdaInvokeVariablesTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testCloneExpandVariables() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("ENV_$ENV_ENV_NAME", "$.$ENV_JSON_PATH"));
        LambdaInvokeVariables variables = new LambdaInvokeVariables(false, "${ENV_ID}", Secret.fromString("$ENV_SECRET}"), "${ENV_REGION}", "${ENV_FUNCTION}", "${\"payload\":\"${ENV_PAYLOAD}\"", true, true, jsonParameterVariables);
        LambdaInvokeVariables clone = variables.getClone();

        EnvVars envVars = new EnvVars();
        envVars.put("ENV_ID", "ID");
        envVars.put("ENV_SECRET", "SECRET");
        envVars.put("ENV_REGION", "eu-west-1");
        envVars.put("ENV_FUNCTION", "FUNCTION");
        envVars.put("ENV_PAYLOAD", "hello");
        envVars.put("ENV_ENV_NAME", "NAME");
        envVars.put("ENV_JSON_PATH", "path");
        envVars.put(".", "bad");
        clone.expandVariables(envVars);

        List<JsonParameterVariables> jsonParameterVariablesExpected = new ArrayList<JsonParameterVariables>();
        jsonParameterVariablesExpected.add(new JsonParameterVariables("ENV_NAME", "$.path"));
        LambdaInvokeVariables expected = new LambdaInvokeVariables(false, "ID", Secret.fromString("$ENV_SECRET}"), "eu-west-1", "FUNCTION", "${\"payload\":\"hello\"", true, true, jsonParameterVariablesExpected);

        assertEquals(expected.getAwsAccessKeyId(), clone.getAwsAccessKeyId());
        assertEquals(expected.getAwsSecretKey(), clone.getAwsSecretKey());
        assertEquals(expected.getAwsRegion(), clone.getAwsRegion());
        assertEquals(expected.getFunctionName(), clone.getFunctionName());
        assertEquals(expected.getPayload(), clone.getPayload());
        assertEquals(expected.getSynchronous(), clone.getSynchronous());
        assertEquals(expected.getSuccessOnly(), clone.getSuccessOnly());
        assertEquals(expected.getJsonParameters().get(0).getEnvVarName(), clone.getJsonParameters().get(0).getEnvVarName());
        assertEquals(expected.getJsonParameters().get(0).getJsonPath(), clone.getJsonParameters().get(0).getJsonPath());
    }

    @Test
    public void testGetInvokeConfig() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("ENV_NAME", "$.path"));
        LambdaInvokeVariables variables = new LambdaInvokeVariables(false, "ID", Secret.fromString("SECRET}"), "eu-west-1", "FUNCTION", "${\"payload\":\"hello\"", true, true, jsonParameterVariables);
        InvokeConfig invokeConfig = variables.getInvokeConfig();

        assertEquals(variables.getFunctionName(), invokeConfig.getFunctionName());
        assertEquals(variables.getPayload(), invokeConfig.getPayload());
        assertEquals(variables.getSynchronous(), invokeConfig.isSynchronous());
        assertEquals(variables.getJsonParameters().get(0).getEnvVarName(), invokeConfig.getJsonParameters().get(0).getEnvVarName());
        assertEquals(variables.getJsonParameters().get(0).getJsonPath(), invokeConfig.getJsonParameters().get(0).getJsonPath());
    }

    @Test
    public void testGetLambdaClientConfig() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("ENV_NAME", "$.path"));
        LambdaInvokeVariables variables = new LambdaInvokeVariables(false, "ID", Secret.fromString("SECRET}"), "eu-west-1", "FUNCTION", "${\"payload\":\"hello\"", true, true, jsonParameterVariables);

        LambdaClientConfig clientConfig = variables.getLambdaClientConfig();
        assertNotNull(clientConfig);
    }
}