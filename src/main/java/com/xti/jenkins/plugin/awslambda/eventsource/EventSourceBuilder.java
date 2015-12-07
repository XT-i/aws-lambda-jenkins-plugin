package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public class EventSourceBuilder {

    private LambdaDeployService lambda;

    private JenkinsLogger logger;

    public EventSourceBuilder(LambdaDeployService lambda, JenkinsLogger logger) {
        this.lambda = lambda;
        this.logger = logger;
    }

    public Boolean createEventSource(EventSourceConfig config) {

        return lambda.deployEventSource(config);
    }
}
