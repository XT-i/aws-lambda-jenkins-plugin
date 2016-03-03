package com.xti.jenkins.plugin.awslambda.upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.TestUtil;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadPublisher;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadVariables;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.Launcher;
import hudson.model.*;
import hudson.util.Secret;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LambdaUploadPublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private TestUtil testUtil = new TestUtil();

    @Test
    @Ignore
    public void testHtml() throws Exception {
        LambdaUploadVariables variables = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "ziplocation", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");
        List<LambdaUploadVariables> variablesList = new ArrayList<>();
        variablesList.add(variables);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaUploadPublisher before = new LambdaUploadPublisher(variablesList);
        p.getPublishersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        LambdaUploadPublisher after = p.getPublishersList().get(LambdaUploadPublisher.class);

        assertEquals(before, after);
    }

    @Mock
    private LambdaUploadVariables original;

    @Mock
    private LambdaClientConfig clientConfig;

    @Mock
    private AWSLambdaClient lambdaClient;

    @Mock
    private LambdaUploadVariables original2;

    @Mock
    private LambdaClientConfig clientConfig2;

    @Mock
    private AWSLambdaClient lambdaClient2;

    @Test
    public void testPerformFolderSuccess() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        setFunctionFound(true);
        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo").child("index.js").copyFrom(new FileInputStream(testUtil.getResource("echo/index.js")));
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Arrays.asList(original, original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformFolderFailure() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        setFunctionFound(true);
        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        LambdaUploadVariables spy2 = Mockito.spy(clone);

        when(original2.getClone()).thenReturn(spy2);
        when(spy2.getLambdaClientConfig()).thenReturn(clientConfig2);
        when(clientConfig2.getClient()).thenReturn(lambdaClient2);

        setFunctionFound(false);
        when(lambdaClient2.createFunction(any(CreateFunctionRequest.class)))
                .thenThrow(new AmazonServiceException("error"));

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo").child("index.js").copyFrom(new FileInputStream(testUtil.getResource("echo/index.js")));
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Arrays.asList(original, original2)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        assertEquals(Result.FAILURE, build.getResult());
    }

    @Test
    public void testPerformZipBuildSuccess() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        setFunctionFound(true);
        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(1)).getFunction(any(GetFunctionRequest.class));
        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformZipBuildUnstableNotSuccessOnly() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "nodejs", "30", false, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        setFunctionFound(true);
        when(original.getClone()).thenReturn(spy);
        when(original.getSuccessOnly()).thenReturn(false);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                build.setResult(Result.UNSTABLE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(1)).getFunction(any(GetFunctionRequest.class));
        assertEquals(Result.UNSTABLE, build.getResult());
    }

    @Test
    public void testPerformZipBuildUnstableSuccessOnly() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        setFunctionFound(true);
        when(original.getClone()).thenReturn(spy);
        when(original.getSuccessOnly()).thenReturn(true);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                build.setResult(Result.UNSTABLE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(0)).getFunction(any(GetFunctionRequest.class));
        assertEquals(Result.UNSTABLE, build.getResult());
    }

    @Test
    public void testPerformZipBuildFailure() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadVariables clone = new LambdaUploadVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "nodejs", "30", true, false, "full", null, false, "", "");

        LambdaUploadVariables spy = Mockito.spy(clone);

        setFunctionFound(true);
        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(lambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                build.setResult(Result.FAILURE);
                return true;
            }
        });
        p.getPublishersList().add(new LambdaUploadPublisher(Collections.singletonList(original)));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        verify(lambdaClient, times(0)).getFunction(any(GetFunctionRequest.class));
        assertEquals(Result.FAILURE, build.getResult());
    }


    private void setFunctionFound(Boolean found){
        if(found) {
            when(lambdaClient.getFunction(any(GetFunctionRequest.class)))
                    .thenReturn(new GetFunctionResult());
        } else {
            when(lambdaClient.getFunction(any(GetFunctionRequest.class)))
                    .thenThrow(new ResourceNotFoundException(""));
        }
    }

}