package com.xti.jenkins.plugin.awslambda.callable;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.service.WorkSpaceZipper;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadBuildStepVariables;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploader;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.remoting.Callable;

import java.io.IOException;

/**
 * Project: aws-lambda
 * Created by Michael on 16/01/2016.
 */
public class DeployCallable implements Callable<Boolean, RuntimeException> {

    private BuildListener listener;
    private FilePath localWorkSpacePath;
    private DeployConfig deployConfig;
    private LambdaClientConfig clientConfig;

    public DeployCallable(BuildListener listener, FilePath localWorkSpacePath, DeployConfig deployConfig, LambdaClientConfig lambdaClientConfig) {
        this.listener = listener;
        this.localWorkSpacePath = localWorkSpacePath;
        this.deployConfig = deployConfig;
        this.clientConfig = lambdaClientConfig;
    }

    @Override
    public Boolean call() throws RuntimeException {

        JenkinsLogger logger = new JenkinsLogger(listener.getLogger());
        LambdaDeployService service = new LambdaDeployService(clientConfig.getClient(), logger);
        WorkSpaceZipper workSpaceZipper = new WorkSpaceZipper(localWorkSpacePath, logger);

        try {
            LambdaUploader lambdaUploader = new LambdaUploader(service, workSpaceZipper, logger);
            return lambdaUploader.upload(deployConfig);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
