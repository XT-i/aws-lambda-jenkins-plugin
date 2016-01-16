package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.TestUtil;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.UpdateModeValue;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LambdaDeployServiceTest {
    private String description;
    private String functionName;
    private String handler;
    private Integer memory;
    private String role;
    private String runtime;
    private Boolean publish;
    private String functionVersion;
    private Boolean createAlias;
    private String alias;
    private Integer timeout;

    @Mock
    private AWSLambdaClient awsLambdaClient;

    @Mock
    private JenkinsLogger jenkinsLogger;

    @Mock
    private WorkSpaceZipper workSpaceZipper;

    private LambdaDeployService lambdaDeployService;
    private TestUtil testUtil = new TestUtil();


    @Before
    public void setUp() throws Exception {
        description = "description";
        functionName = "function";
        handler = "function.handler";
        memory = 1024;
        role = "role";
        runtime = "nodejs";
        publish = Boolean.FALSE;
        functionVersion = null;
        createAlias = Boolean.FALSE;
        alias = null;
        timeout = 30;
        lambdaDeployService = new LambdaDeployService(awsLambdaClient, jenkinsLogger);
        when(awsLambdaClient.updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class)))
                .thenReturn(new UpdateFunctionConfigurationResult());
        when(awsLambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(new UpdateFunctionCodeResult());
        when(awsLambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(new CreateFunctionResult());
    }

    @Test
    public void testExistsNoZipFull() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(true);
        assertFalse(result);
    }

    @Test
    public void testExistsNoZipCode() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Code);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertFalse(result);
    }

    @Test
    public void testExistsNoZipConfig() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Config);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(true);
        assertTrue(result);
    }

    @Test
    public void testExistsZipFull() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(true);
        calledUpdateConfiguration(true);
        assertTrue(result);
    }

    @Test
    public void testExistZipFullPublish() throws Exception {
        setFunctionFound(true);
        setPublishVersion("1");
        DeployConfig deployConfig = getDeployConfig();
        deployConfig.setPublish(true);
        Boolean result = lambdaDeployService.deployLambda(deployConfig, getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledCreateAlias(false);
        calledUpdateCode(true);
        calledUpdateConfiguration(true);
        assertTrue(result);
    }

    @Test
    public void testExistZipFullPublishNewAlias() throws Exception {
        setFunctionFound(true);
        setPublishVersion("1");
        setAlias("myFunc", false);
        DeployConfig deployConfig = getDeployConfig();
        deployConfig.setPublish(publish);
        deployConfig.setCreateAlias(createAlias);
        deployConfig.setAlias(alias);
        Boolean result = lambdaDeployService.deployLambda(deployConfig, getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledCreateAlias(true);
        calledUpdateCode(true);
        calledUpdateConfiguration(true);
        assertTrue(result);
    }

    @Test
    public void testExistZipFullPublishUpdateAlias() throws Exception {
        setFunctionFound(true);
        setPublishVersion("1");
        setAlias("myFunc", true);
        DeployConfig deployConfig = getDeployConfig();
        deployConfig.setPublish(publish);
        deployConfig.setCreateAlias(createAlias);
        deployConfig.setAlias(alias);
        Boolean result = lambdaDeployService.deployLambda(deployConfig, getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledCreateAlias(false);
        calledUpdateCode(true);
        calledUpdateConfiguration(true);
        calledUpdateAlias(true);
        assertTrue(result);
    }

    @Test
    public void testExistsZipCode() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Code);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(true);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void testExistsZipConfig() throws Exception {
        setFunctionFound(true);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Config);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(true);
        assertTrue(result);
    }

    @Test
    public void testNewNoZipFull() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertFalse(result);
    }

    @Test
    public void testNewNoZipCode() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Code);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertFalse(result);
    }

    @Test
    public void testNewNoZipConfig() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), null, UpdateModeValue.Config);

        calledGetFunction();
        calledCreateFunction(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertFalse(result);
    }

    @Test
    public void testNewZipFull() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(true);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void testNewZipFullPublish() throws Exception {
        setFunctionFound(false);
        setPublishVersion("1");
        DeployConfig deployConfig = getDeployConfig();
        deployConfig.setPublish(true);
        Boolean result = lambdaDeployService.deployLambda(deployConfig, getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(true);
        calledCreateAlias(false);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void testNewZipFullPublishNewAlias() throws Exception {
        setFunctionFound(false);
        setPublishVersion("1");
        setAlias("myFunc", false);
        DeployConfig deployConfig = getDeployConfig();
        deployConfig.setPublish(publish);
        deployConfig.setCreateAlias(createAlias);
        deployConfig.setAlias(alias);
        Boolean result = lambdaDeployService.deployLambda(deployConfig, getFunctionCode(), UpdateModeValue.Full);

        calledGetFunction();
        calledCreateFunction(true);
        calledCreateAlias(true);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void testNewZipCode() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Code);

        calledGetFunction();
        calledCreateFunction(true);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void testNewZipConfig() throws Exception {
        setFunctionFound(false);
        Boolean result = lambdaDeployService.deployLambda(getDeployConfig(), getFunctionCode(), UpdateModeValue.Config);

        calledGetFunction();
        calledCreateFunction(true);
        calledUpdateCode(false);
        calledUpdateConfiguration(false);
        assertTrue(result);
    }

    @Test
    public void getFunctionCodeFile() throws IOException, InterruptedException {
        File file = testUtil.getResource("echo.zip");

        when(workSpaceZipper.getZip(any(String.class))).thenReturn(file);

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("location.zip", workSpaceZipper);

        assertEquals(getFunctionZip(file), functionCode.getZipFile());
        assertNull(functionCode.getS3Bucket());
        assertNull(functionCode.getS3Key());
        assertNull(functionCode.getS3ObjectVersion());

    }

    @Test
    public void getFunctionCodeS3() throws IOException, InterruptedException {

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("s3://bucket/key/subkey1.zip", workSpaceZipper);

        verify(workSpaceZipper, times(0)).getZip(any(String.class));
        assertEquals("bucket", functionCode.getS3Bucket());
        assertEquals("key/subkey1.zip", functionCode.getS3Key());
        assertNull(functionCode.getS3ObjectVersion());
        assertNull(functionCode.getZipFile());
    }

    @Test
    public void getFunctionCodeS3ShortKey() throws IOException, InterruptedException {

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("s3://bucket/a", workSpaceZipper);

        verify(workSpaceZipper, times(0)).getZip(any(String.class));
        assertEquals("bucket", functionCode.getS3Bucket());
        assertEquals("a", functionCode.getS3Key());
        assertNull(functionCode.getS3ObjectVersion());
        assertNull(functionCode.getZipFile());
    }

    @Test
    public void getFunctionCodeS3NoKeyWithSlash() throws IOException, InterruptedException {

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("s3://bucket/", workSpaceZipper);

        verify(workSpaceZipper, times(0)).getZip(any(String.class));
        assertEquals("bucket", functionCode.getS3Bucket());
        assertNull(functionCode.getS3Key());
        assertNull(functionCode.getS3ObjectVersion());
        assertNull(functionCode.getZipFile());
    }

    @Test
    public void getFunctionCodeS3NoKeyWithoutSlash() throws IOException, InterruptedException {

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("s3://bucket", workSpaceZipper);

        verify(workSpaceZipper, times(0)).getZip(any(String.class));
        assertNull(functionCode.getS3Bucket());
        assertNull(functionCode.getS3Key());
        assertNull(functionCode.getS3ObjectVersion());
        assertNull(functionCode.getZipFile());
    }

    @Test
    public void getFunctionCodeS3Version() throws IOException, InterruptedException {

        FunctionCode functionCode = lambdaDeployService.getFunctionCode("s3://bucket/key/subkey1.zip?versionId=abc/def|123", workSpaceZipper);

        verify(workSpaceZipper, times(0)).getZip(any(String.class));
        assertEquals("bucket", functionCode.getS3Bucket());
        assertEquals("key/subkey1.zip", functionCode.getS3Key());
        assertEquals("abc/def|123", functionCode.getS3ObjectVersion());
        assertNull(functionCode.getZipFile());
    }

    private void calledGetFunction(){
        verify(awsLambdaClient, times(1)).getFunction(any(GetFunctionRequest.class));
    }

    private void calledUpdateCode(Boolean called){
        if(called){
            ArgumentCaptor<UpdateFunctionCodeRequest> args = ArgumentCaptor.forClass(UpdateFunctionCodeRequest.class);
            verify(awsLambdaClient, times(1)).updateFunctionCode(args.capture());
            UpdateFunctionCodeRequest expected = null;
            try {
                expected = new UpdateFunctionCodeRequest()
                        .withFunctionName(functionName)
                        .withPublish(publish)
                        .withZipFile(ByteBuffer.wrap(FileUtils.readFileToByteArray(getZipFile())));
            } catch (IOException e) {
                fail("Couldn't process echo.zip");
            }
            assertEquals(expected, args.getValue());

        } else {
            verify(awsLambdaClient, never()).updateFunctionCode(any(UpdateFunctionCodeRequest.class));
        }
    }

    private void calledUpdateConfiguration(Boolean called){
        if(called){
            ArgumentCaptor<UpdateFunctionConfigurationRequest> args = ArgumentCaptor.forClass(UpdateFunctionConfigurationRequest.class);
            verify(awsLambdaClient, times(1)).updateFunctionConfiguration(args.capture());
            UpdateFunctionConfigurationRequest expected = new UpdateFunctionConfigurationRequest()
                    .withDescription(description)
                    .withFunctionName(functionName)
                    .withHandler(handler)
                    .withMemorySize(memory)
                    .withRole(role)
                    .withTimeout(timeout);
            assertEquals(expected, args.getValue());

        } else {
            verify(awsLambdaClient, never()).updateFunctionConfiguration(any(UpdateFunctionConfigurationRequest.class));
        }
    }

    private void calledCreateFunction(Boolean called) {
        if (called) {
            ArgumentCaptor<CreateFunctionRequest> args = ArgumentCaptor.forClass(CreateFunctionRequest.class);
            verify(awsLambdaClient, times(1)).createFunction(args.capture());
            try {
                CreateFunctionRequest expected = new CreateFunctionRequest()
                        .withDescription(description)
                        .withFunctionName(functionName)
                        .withHandler(handler)
                        .withMemorySize(memory)
                        .withRole(role)
                        .withTimeout(timeout)
                        .withPublish(publish)
                        .withRuntime(runtime)
                        .withCode(new FunctionCode().withZipFile(ByteBuffer.wrap(FileUtils.readFileToByteArray(getZipFile()))));
                assertEquals(expected, args.getValue());

            } catch (IOException e) {
                fail("Couldn't process echo.zip");
            }
        } else {
            verify(awsLambdaClient, never()).createFunction(any(CreateFunctionRequest.class));
        }
    }

    private void calledCreateAlias(Boolean called){
        if(called){
            ArgumentCaptor<CreateAliasRequest> args = ArgumentCaptor.forClass(CreateAliasRequest.class);
            verify(awsLambdaClient, times(1)).createAlias(args.capture());
            CreateAliasRequest expected = new CreateAliasRequest()
                    .withFunctionName(functionName)
                    .withFunctionVersion(functionVersion)
                    .withName(alias);

            assertEquals(expected, args.getValue());
        } else {
            verify(awsLambdaClient, never()).createAlias(any(CreateAliasRequest.class));
        }
    }

    private void calledUpdateAlias(Boolean called){
        if(called){
            ArgumentCaptor<UpdateAliasRequest> args = ArgumentCaptor.forClass(UpdateAliasRequest.class);
            verify(awsLambdaClient, times(1)).updateAlias(args.capture());
            UpdateAliasRequest expected = new UpdateAliasRequest()
                    .withFunctionName(functionName)
                    .withFunctionVersion(functionVersion)
                    .withName(alias);

            assertEquals(expected, args.getValue());
        } else {
            verify(awsLambdaClient, never()).updateAlias(any(UpdateAliasRequest.class));
        }
    }

    private DeployConfig getDeployConfig(){
        return new DeployConfig(null, description, functionName, handler, memory, role, runtime, timeout, null, false, null, false);
    }

    private File getZipFile(){
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("echo.zip");
        if(resource != null){
            return new File(resource.getFile());
        } else {
            throw new IllegalStateException("Could not load echo.zip");
        }
    }

    private FunctionCode getFunctionCode() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("echo.zip");
        if(resource != null){
            File zipFile = new File(resource.getFile());
            return new FunctionCode()
                    .withZipFile(getFunctionZip(zipFile));
        } else {
            throw new IllegalStateException("Could not load echo.zip");
        }
    }

    private void setFunctionFound(Boolean found){
        if(found) {
            when(awsLambdaClient.getFunction(any(GetFunctionRequest.class)))
                    .thenReturn(new GetFunctionResult());
        } else {
            when(awsLambdaClient.getFunction(any(GetFunctionRequest.class)))
                    .thenThrow(new ResourceNotFoundException(""));
        }
    }

    private void setPublishVersion(String version){
        this.functionVersion = version;
        this.publish = true;
        CreateFunctionResult createFunctionResult = new CreateFunctionResult()
                .withVersion(version);
        when(awsLambdaClient.createFunction(any(CreateFunctionRequest.class)))
                .thenReturn(createFunctionResult);
        UpdateFunctionCodeResult updateFunctionCodeResult = new UpdateFunctionCodeResult()
                .withVersion(version);
        when(awsLambdaClient.updateFunctionCode(any(UpdateFunctionCodeRequest.class)))
                .thenReturn(updateFunctionCodeResult);
    }

    private void setAlias(String alias, Boolean exists){
        this.createAlias = Boolean.TRUE;
        this.alias = alias;

        GetAliasResult getAliasResult = new GetAliasResult()
                .withName(alias)
                .withFunctionVersion(functionVersion);
        if(exists){
            when(awsLambdaClient.getAlias(any(GetAliasRequest.class)))
                    .thenReturn(getAliasResult);
        } else {
            when(awsLambdaClient.getAlias(any(GetAliasRequest.class)))
                    .thenThrow(new ResourceNotFoundException(""));
        }

        CreateAliasResult createAliasResult = new CreateAliasResult()
                .withName(alias)
                .withFunctionVersion(functionVersion);
        when(awsLambdaClient.createAlias(any(CreateAliasRequest.class)))
                .thenReturn(createAliasResult);

        UpdateAliasResult updateAliasResult = new UpdateAliasResult()
                .withName(alias)
                .withFunctionVersion(functionVersion);
        when(awsLambdaClient.updateAlias(any(UpdateAliasRequest.class)))
                .thenReturn(updateAliasResult);
    }

    private ByteBuffer getFunctionZip(File zipFile) throws IOException {
        return ByteBuffer.wrap(FileUtils.readFileToByteArray(zipFile));
    }
}