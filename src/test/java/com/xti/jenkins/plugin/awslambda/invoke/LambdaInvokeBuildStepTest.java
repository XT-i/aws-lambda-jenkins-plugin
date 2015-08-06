package com.xti.jenkins.plugin.awslambda.invoke;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.util.LogTaskListener;
import hudson.util.Secret;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LambdaInvokeBuildStepTest {

    private static final Logger LOGGER = Logger.getLogger(LambdaInvokeBuildStepTest.class.getName());


    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Ignore
    @Test
    public void testHtml() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "value"));
        LambdaInvokeBuildStepVariables variables = new LambdaInvokeBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, jsonParameterVariables);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaInvokeBuildStep before = new LambdaInvokeBuildStep(variables);
        p.getBuildersList().add(before);

        j.submit(j.createWebClient().getPage(p, "configure").getFormByName("config"));

        LambdaInvokeBuildStep after = p.getBuildersList().get(LambdaInvokeBuildStep.class);

        assertEquals(before, after);
    }

    @Mock
    private LambdaInvokeBuildStepVariables original;

    @Mock
    private LambdaClientConfig clientConfig;

    @Mock
    private AWSLambdaClient lambdaClient;

    @Test
    public void testPerform() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeBuildStepVariables clone = new LambdaInvokeBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, jsonParameterVariables);
        LambdaInvokeBuildStepVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);
        final String logBase64 = "bGFtYmRh";
        final String responsePayload = "{\"key2\": \"value2\"}";

        InvokeResult invokeResult = new InvokeResult()
                .withLogResult(logBase64)
                .withPayload(ByteBuffer.wrap(responsePayload.getBytes()));

        when(lambdaClient.invoke(any(InvokeRequest.class)))
                .thenReturn(invokeResult);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new LambdaInvokeBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();
        EnvVars environment = build.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));

        assertEquals("value2", environment.get("KEY"));
        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformFailure() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeBuildStepVariables clone = new LambdaInvokeBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, jsonParameterVariables);
        LambdaInvokeBuildStepVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);
        final String logBase64 = "bGFtYmRh";
        final String responsePayload = "{\"errorMessage\":\"event_fail\"}";

        InvokeResult invokeResult = new InvokeResult()
                .withLogResult(logBase64)
                .withPayload(ByteBuffer.wrap(responsePayload.getBytes()))
                .withFunctionError("Unhandled");

        when(lambdaClient.invoke(any(InvokeRequest.class)))
                .thenReturn(invokeResult);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new LambdaInvokeBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();
        EnvVars environment = build.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));

        assertEquals(null, environment.get("KEY"));
        assertEquals(Result.FAILURE, build.getResult());
    }
}