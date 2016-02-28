package com.xti.jenkins.plugin.awslambda.callable;

import com.xti.jenkins.plugin.awslambda.invoke.InvokeConfig;
import com.xti.jenkins.plugin.awslambda.invoke.LambdaInvocationResult;
import com.xti.jenkins.plugin.awslambda.invoke.LambdaInvoker;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.service.LambdaInvokeService;
import com.xti.jenkins.plugin.awslambda.service.WorkSpaceZipper;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploader;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.FilePath;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.remoting.Callable;

import java.io.IOException;

/**
 * Project: aws-lambda
 * Created by Michael on 16/01/2016.
 */
public class InvokeCallable implements Callable<LambdaInvocationResult, RuntimeException> {

    private TaskListener listener;
    private InvokeConfig invokeConfig;
    private LambdaClientConfig clientConfig;

    public InvokeCallable(TaskListener listener, InvokeConfig invokeConfig, LambdaClientConfig lambdaClientConfig) {
        this.listener = listener;
        this.invokeConfig = invokeConfig;
        this.clientConfig = lambdaClientConfig;
    }

    @Override
    public LambdaInvocationResult call() throws RuntimeException {

        JenkinsLogger logger = new JenkinsLogger(listener.getLogger());
        LambdaInvokeService service = new LambdaInvokeService(clientConfig.getClient(), logger);
        LambdaInvoker lambdaInvoker = new LambdaInvoker(service, logger);

        try {
            return lambdaInvoker.invoke(invokeConfig);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
