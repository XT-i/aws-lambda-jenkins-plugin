package com.xti.jenkins.plugin.awslambda.invoke;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.*;
import hudson.util.LogTaskListener;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LambdaInvokePublisherTest {

    private static final Logger LOGGER = Logger.getLogger(LambdaInvokePublisherTest.class.getName());


    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testHtml() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "value"));
        LambdaInvokeVariables variables = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        List<LambdaInvokeVariables> variablesList = new ArrayList<LambdaInvokeVariables>();
        variablesList.add(variables);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaInvokePublisher before = new LambdaInvokePublisher(variablesList);
        p.getPublishersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        LambdaInvokePublisher after = p.getPublishersList().get(LambdaInvokePublisher.class);

        assertEquals(before, after);
    }

    @Mock
    private LambdaInvokeVariables original;

    @Mock
    private LambdaClientConfig clientConfig;

    @Mock
    private AWSLambdaClient lambdaClient;

    @Mock
    private LambdaInvokeVariables original2;

    @Mock
    private LambdaClientConfig clientConfig2;

    @Mock
    private AWSLambdaClient lambdaClient2;

    @Test
    public void testPerform() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeVariables clone = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        LambdaInvokeVariables spy = Mockito.spy(clone);

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
        p.getPublishersList().add(new LambdaInvokePublisher(Arrays.asList(original, original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();
        EnvVars environment = build.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));

        assertEquals("value2", environment.get("KEY"));
        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformBuildUnstableNotSuccessOnly() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeVariables clone = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, false, jsonParameterVariables);
        LambdaInvokeVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(original.getSuccessOnly()).thenReturn(false);
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
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.setResult(Result.UNSTABLE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaInvokePublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(1)).invoke(any(InvokeRequest.class));
        assertEquals(Result.UNSTABLE, build.getResult());
    }

    @Test
    public void testPerformBuildUnstableSuccessOnly() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeVariables clone = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        LambdaInvokeVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(original.getSuccessOnly()).thenReturn(true);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.setResult(Result.UNSTABLE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaInvokePublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(0)).invoke(any(InvokeRequest.class));
        assertEquals(Result.UNSTABLE, build.getResult());
    }

    @Test
    public void testPerformBuildFailure() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeVariables clone = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        LambdaInvokeVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(original.getSuccessOnly()).thenReturn(true);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.setResult(Result.FAILURE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaInvokePublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(0)).invoke(any(InvokeRequest.class));
        assertEquals(Result.FAILURE, build.getResult());
    }

    @Test
    public void testPerformFailure() throws IOException, ExecutionException, InterruptedException {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "$.key2"));
        LambdaInvokeVariables clone = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        LambdaInvokeVariables spy = Mockito.spy(clone);

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


        LambdaInvokeVariables spy2 = Mockito.spy(clone);
        when(original2.getClone()).thenReturn(spy2);
        when(spy2.getLambdaClientConfig()).thenReturn(clientConfig2);
        when(clientConfig2.getClient()).thenReturn(lambdaClient2);
        final String responsePayload2 = "{\"errorMessage\":\"event_fail\"}";

        InvokeResult invokeResult2 = new InvokeResult()
                .withLogResult(logBase64)
                .withPayload(ByteBuffer.wrap(responsePayload2.getBytes()))
                .withFunctionError("Unhandled");

        when(lambdaClient2.invoke(any(InvokeRequest.class)))
                .thenReturn(invokeResult2);


        FreeStyleProject p = j.createFreeStyleProject();
        p.getPublishersList().add(new LambdaInvokePublisher(Arrays.asList(original, original2)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();
        EnvVars environment = build.getEnvironment(new LogTaskListener(LOGGER, Level.INFO));

        assertEquals("value2", environment.get("KEY"));
        assertEquals(Result.FAILURE, build.getResult());
    }
}