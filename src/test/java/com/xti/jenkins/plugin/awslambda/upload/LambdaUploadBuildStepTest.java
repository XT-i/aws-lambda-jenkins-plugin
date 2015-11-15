package com.xti.jenkins.plugin.awslambda.upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.TestUtil;
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
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LambdaUploadBuildStepTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private TestUtil testUtil = new TestUtil();

    @Test
    @Ignore
    public void testHtml() throws Exception {
        LambdaUploadBuildStepVariables variables = new LambdaUploadBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "ziplocation", "description", "function", "handler", "1024", "role", "nodejs", "30", "full", false, null, false, null, null);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaUploadBuildStep before = new LambdaUploadBuildStep(variables);
        p.getBuildersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        LambdaUploadBuildStep after = p.getBuildersList().get(LambdaUploadBuildStep.class);

        assertEquals(before, after);
    }

    @Mock
    private LambdaUploadBuildStepVariables original;

    @Mock
    private LambdaClientConfig clientConfig;

    @Mock
    private AWSLambdaClient lambdaClient;

    @Test
    public void testPerformFolderSuccess() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadBuildStepVariables clone = new LambdaUploadBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo", "description", "function", "handler", "1024", "role", "nodejs", "30", "full", false, null, false, null, null);

        LambdaUploadBuildStepVariables spy = Mockito.spy(clone);

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
        p.getBuildersList().add(new LambdaUploadBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformFolderFailure() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadBuildStepVariables clone = new LambdaUploadBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo", "description", "function", null, "1024", "role", "nodejs", "30", "full", false, null, false, null, null);

        LambdaUploadBuildStepVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        setFunctionFound(false);
        when(lambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenThrow(new AmazonServiceException("error"));

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo").child("index.js").copyFrom(new FileInputStream(testUtil.getResource("echo/index.js")));
                return true;
            }
        });
        p.getBuildersList().add(new LambdaUploadBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        assertEquals(Result.FAILURE, build.getResult());
    }

    @Test
    public void testPerformZipSuccess() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadBuildStepVariables clone = new LambdaUploadBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "nodejs", "30", "full", false, null, false, null, null);

        LambdaUploadBuildStepVariables spy = Mockito.spy(clone);

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
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                return true;
            }
        });
        p.getBuildersList().add(new LambdaUploadBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

        assertEquals(Result.SUCCESS, build.getResult());
    }

    @Test
    public void testPerformZipFailure() throws IOException, ExecutionException, InterruptedException {
        LambdaUploadBuildStepVariables clone = new LambdaUploadBuildStepVariables(false, "accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "echo.zip", "description", "function", "handler", "1024", "role", "c#", "30", "config", false, null, false, null, null);

        LambdaUploadBuildStepVariables spy = Mockito.spy(clone);

        when(original.getClone()).thenReturn(spy);
        when(spy.getLambdaClientConfig()).thenReturn(clientConfig);
        when(clientConfig.getClient()).thenReturn(lambdaClient);

        setFunctionFound(true);
        when(lambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenThrow(new AmazonServiceException("error"));

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                                   BuildListener listener) throws InterruptedException, IOException {
                build.getWorkspace().child("echo.zip").copyFrom(new FileInputStream(testUtil.getResource("echo.zip")));
                return true;
            }
        });
        p.getBuildersList().add(new LambdaUploadBuildStep(original));
        FreeStyleBuild build = p.scheduleBuild2(0).get();

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