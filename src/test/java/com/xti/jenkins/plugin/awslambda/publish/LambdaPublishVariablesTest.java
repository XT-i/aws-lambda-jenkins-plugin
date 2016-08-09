package com.xti.jenkins.plugin.awslambda.publish;

import com.amazonaws.services.lambda.AWSLambda;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Magnus Sulland on 4/08/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class LambdaPublishVariablesTest {

    @Test
    public void testCloneExpandVariables() throws Exception {
        LambdaPublishVariables variables = new LambdaPublishVariables(false, "${ENV_ID}", Secret.fromString("$ENV_SECRET}}"), "${ENV_REGION}", "${ENV_ARN}", "${ENV_ALIAS}", "description ${ENV_VERSIONDESCRIPTION}");
        LambdaPublishVariables clone = variables.getClone();

        EnvVars envVars = new EnvVars();
        envVars.put("ENV_ID", "ID");
        envVars.put("ENV_SECRET", "SECRET");
        envVars.put("ENV_REGION", "eu-west-1");
        envVars.put("ENV_ARN", "ARN");
        envVars.put("ENV_ALIAS", "ALIAS");
        envVars.put("ENV_VERSIONDESCRIPTION", "DESCRIPTION");
        clone.expandVariables(envVars);

        LambdaPublishVariables expected = new LambdaPublishVariables(false, "ID", Secret.fromString("$ENV_SECRET}}"), "eu-west-1", "ARN", "ALIAS", "description DESCRIPTION");

        assertEquals(expected.getAwsAccessKeyId(), clone.getAwsAccessKeyId());
        assertEquals(expected.getAwsSecretKey(), clone.getAwsSecretKey());
        assertEquals(expected.getAwsRegion(), clone.getAwsRegion());
        assertEquals(expected.getFunctionARN(), clone.getFunctionARN());
        assertEquals(expected.getFunctionAlias(), clone.getFunctionAlias());
        assertEquals(expected.getVersionDescription(), clone.getVersionDescription());
    }

    @Test
    public void testGetPublishConfig() throws Exception {
        LambdaPublishVariables variables = new LambdaPublishVariables("eu-west-1", "ARN", "ALIAS", "DESCRIPTION");
        PublishConfig publishConfig = variables.getPublishConfig();

        assertEquals(variables.getFunctionAlias(), publishConfig.getFunctionAlias());
        assertEquals(variables.getFunctionARN(), publishConfig.getFunctionARN());
        assertEquals(variables.getVersionDescription(), publishConfig.getVersionDescription());
    }

    @Test
    public void testGetLambdaClientConfig() throws Exception {
        LambdaPublishVariables variables = new LambdaPublishVariables(false, "KEY", Secret.fromString("SECRET}"), "eu-west-1", "ARN", "ALIAS", "DESCRIPTION");
        variables.expandVariables(new EnvVars());
        LambdaClientConfig lambdaClientConfig = variables.getLambdaClientConfig();

        AWSLambda lambda = lambdaClientConfig.getClient();
        assertNotNull(lambda);
    }
}
