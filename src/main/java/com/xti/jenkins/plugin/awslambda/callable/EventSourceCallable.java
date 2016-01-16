package com.xti.jenkins.plugin.awslambda.callable;

import com.xti.jenkins.plugin.awslambda.eventsource.EventSourceBuilder;
import com.xti.jenkins.plugin.awslambda.eventsource.EventSourceConfig;
import com.xti.jenkins.plugin.awslambda.invoke.InvokeConfig;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.model.BuildListener;
import hudson.remoting.Callable;

/**
 * Project: aws-lambda
 * Created by Michael on 16/01/2016.
 */
public class EventSourceCallable implements Callable<Boolean, RuntimeException> {

    private BuildListener listener;
    private EventSourceConfig eventSourceConfig;
    private LambdaClientConfig clientConfig;

    public EventSourceCallable(BuildListener listener, EventSourceConfig eventSourceConfig, LambdaClientConfig clientConfig) {
        this.listener = listener;
        this.eventSourceConfig = eventSourceConfig;
        this.clientConfig = clientConfig;
    }

    @Override
    public Boolean call() throws RuntimeException {
        JenkinsLogger logger = new JenkinsLogger(listener.getLogger());
        LambdaDeployService service = new LambdaDeployService(clientConfig.getClient(), logger);
        EventSourceBuilder builder = new EventSourceBuilder(service, logger);

        return builder.createEventSource(eventSourceConfig);
    }
}
