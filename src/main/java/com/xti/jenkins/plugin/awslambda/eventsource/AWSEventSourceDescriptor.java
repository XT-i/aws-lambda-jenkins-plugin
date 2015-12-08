package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import hudson.model.Describable;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public abstract class AWSEventSourceDescriptor<T extends Describable<T>> extends AWSLambdaDescriptor<T> {

}
