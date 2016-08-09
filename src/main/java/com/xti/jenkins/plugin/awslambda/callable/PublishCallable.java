package com.xti.jenkins.plugin.awslambda.callable;

import com.xti.jenkins.plugin.awslambda.publish.LambdaPublishServiceResponse;
import com.xti.jenkins.plugin.awslambda.publish.LambdaPublisher;
import com.xti.jenkins.plugin.awslambda.publish.PublishConfig;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaPublishService;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.model.TaskListener;
import hudson.remoting.Callable;
import org.jenkinsci.remoting.RoleChecker;

import java.io.IOException;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class PublishCallable implements Callable<LambdaPublishServiceResponse, RuntimeException> {

    private TaskListener listener;
    private PublishConfig publishConfig;
    private LambdaClientConfig clientConfig;

    public PublishCallable(TaskListener listener, PublishConfig publishConfig, LambdaClientConfig lambdaClientConfig) {
        this.listener = listener;
        this.publishConfig = publishConfig;
        this.clientConfig = lambdaClientConfig;
    }

    @Override
    public LambdaPublishServiceResponse call() throws RuntimeException {

        JenkinsLogger logger = new JenkinsLogger(listener.getLogger());
        LambdaPublishService service = new LambdaPublishService(clientConfig.getClient(), logger);

        try {
            LambdaPublisher publishPublisher = new LambdaPublisher(service, logger);
            return publishPublisher.publish(publishConfig);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkRoles(RoleChecker roleChecker) throws SecurityException {
        //ignore for now
    }
}
