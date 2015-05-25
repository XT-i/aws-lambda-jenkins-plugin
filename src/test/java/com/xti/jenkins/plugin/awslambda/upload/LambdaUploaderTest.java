package com.xti.jenkins.plugin.awslambda.upload;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.service.WorkSpaceZipper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LambdaUploaderTest {

    @Mock
    private LambdaDeployService service;

    @Mock
    private WorkSpaceZipper zipper;

    @Mock
    private JenkinsLogger logger;


    @Test
    public void testUploadSuccess() throws Exception {
        DeployConfig deployConfig = new DeployConfig("location", "description", "function", "handler", 1024, "role", "runtime", 30, "full");
        File file = new File("path");

        when(zipper.getZip(any(String.class))).thenReturn(file);
        when(service.deployLambda(any(DeployConfig.class), any(File.class), any(UpdateModeValue.class)))
                .thenReturn(true);

        LambdaUploader uploader = new LambdaUploader(service, zipper, logger);
        Boolean uploaded = uploader.upload(deployConfig);

        verify(logger, times(1)).log("%nStarting lambda deployment procedure");
        verify(service, times(1)).deployLambda(deployConfig, file, UpdateModeValue.Full);
        assertTrue(uploaded);
    }

    @Test
    public void testUploadFailure() throws Exception {
        DeployConfig deployConfig = new DeployConfig("location", "description", "function", "handler", 1024, "role", "runtime", 30, "full");
        File file = new File("path");

        when(zipper.getZip(any(String.class))).thenReturn(file);
        when(service.deployLambda(any(DeployConfig.class), any(File.class), any(UpdateModeValue.class)))
                .thenReturn(false);

        LambdaUploader uploader = new LambdaUploader(service, zipper, logger);
        Boolean uploaded = uploader.upload(deployConfig);

        verify(logger, times(1)).log("%nStarting lambda deployment procedure");
        verify(service, times(1)).deployLambda(deployConfig, file, UpdateModeValue.Full);
        assertFalse(uploaded);
    }

    @Test
    public void testUploadNoArtifact() throws Exception {
        DeployConfig deployConfig = new DeployConfig("location", "description", "function", "handler", 1024, "role", "runtime", 30, "full");

        when(zipper.getZip(any(String.class))).thenReturn(null);
        when(service.deployLambda(any(DeployConfig.class), any(File.class), any(UpdateModeValue.class)))
                .thenReturn(true);

        LambdaUploader uploader = new LambdaUploader(service, zipper, logger);
        Boolean uploaded = uploader.upload(deployConfig);

        verify(logger, times(1)).log("%nStarting lambda deployment procedure");
        verify(service, times(1)).deployLambda(deployConfig, null, UpdateModeValue.Full);
        assertTrue(uploaded);
    }
}
