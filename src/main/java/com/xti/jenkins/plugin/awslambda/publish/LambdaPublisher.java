package com.xti.jenkins.plugin.awslambda.publish;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaPublishService;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.io.IOException;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublisher extends Notifier {
    private LambdaPublishService lambda;
    private JenkinsLogger logger;

    public LambdaPublisher(LambdaPublishService lambda, JenkinsLogger logger) throws IOException, InterruptedException {
        this.lambda = lambda;
        this.logger = logger;
    }

    public LambdaPublishServiceResponse publish(PublishConfig config) throws IOException, InterruptedException {
        logger.log("%nStarting lambda publish procedure");
        return lambda.publishLambda(config);
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

}