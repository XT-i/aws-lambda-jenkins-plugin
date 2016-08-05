package com.xti.jenkins.plugin.awslambda.publish;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.invoke.LambdaInvokePublisher;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.LogTaskListener;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Sulland on 4/08/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class LambdaPublishPublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Mock
    private LambdaPublishVariables original;

    @Mock
    private LambdaClientConfig clientConfig;

    @Mock
    private AWSLambdaClient lambdaClient;

    @Test
    public void testPerform() throws IOException, ExecutionException, InterruptedException {
        LambdaPublishVariables clone = new LambdaPublishVariables(false, "${ENV_ID}", Secret.fromString("$ENV_SECRET}}"), "${ENV_REGION}", "${ENV_ARN}", "${ENV_ALIAS}", "description ${ENV_VERSIONDESCRIPTION}");
        LambdaPublishVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        PublishVersionResult publishResult = new PublishVersionResult()
                .withFunctionArn("ARN")
                .withDescription("DESCRIPTION")
                .withVersion("VERSION");

        when(lambdaClient.publishVersion(any(PublishVersionRequest.class)))
                .thenReturn(publishResult);

        UpdateAliasResult aliasResult = new UpdateAliasResult()
                .withDescription("DESCRIPTION")
                .withFunctionVersion("VERSION")
                .withName("ALIAS");

        when(lambdaClient.updateAlias(any(UpdateAliasRequest.class)))
                .thenReturn(aliasResult);

        GetAliasResult aliasFindResult = new GetAliasResult();

        when(lambdaClient.getAlias(any(GetAliasRequest.class)))
                .thenReturn(aliasFindResult);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getPublishersList().add(new LambdaPublishPublisher(Arrays.asList(original, original)));

        FreeStyleBuild build = p.scheduleBuild2(0).get();
        assertEquals(Result.SUCCESS, build.getResult());
    }


    @Test
    public void testPerformFailure() throws IOException, ExecutionException, InterruptedException {
        LambdaPublishVariables clone = new LambdaPublishVariables(false, "${ENV_ID}", Secret.fromString("$ENV_SECRET}}"), "${ENV_REGION}", "${ENV_ARN}", "${ENV_ALIAS}", "description ${ENV_VERSIONDESCRIPTION}");
        LambdaPublishVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        PublishVersionResult publishResult = new PublishVersionResult()
                .withFunctionArn("ARN")
                .withDescription("DESCRIPTION")
                .withVersion("VERSION");

        when(lambdaClient.publishVersion(any(PublishVersionRequest.class)))
                .thenReturn(publishResult);

        UpdateAliasResult aliasResult = new UpdateAliasResult()
                .withDescription("DESCRIPTION")
                .withFunctionVersion("VERSION")
                .withName("ALIAS");

        when(lambdaClient.updateAlias(any(UpdateAliasRequest.class)))
                .thenReturn(aliasResult);

        GetAliasResult aliasFindResult = new GetAliasResult();

        when(lambdaClient.getAlias(any(GetAliasRequest.class)))
                .thenThrow(new ResourceNotFoundException(""));

        FreeStyleProject p = j.createFreeStyleProject();
        p.getPublishersList().add(new LambdaPublishPublisher(Arrays.asList(original, original)));

        FreeStyleBuild build = p.scheduleBuild2(0).get();
        assertEquals(Result.FAILURE, build.getResult());
    }
}
