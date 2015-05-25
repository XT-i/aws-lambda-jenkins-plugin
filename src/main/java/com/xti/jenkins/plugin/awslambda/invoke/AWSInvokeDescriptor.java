package com.xti.jenkins.plugin.awslambda.invoke;

import com.xti.jenkins.plugin.awslambda.AWSLambdaDescriptor;
import hudson.model.Describable;

public abstract class AWSInvokeDescriptor<T extends Describable<T>> extends AWSLambdaDescriptor<T> {

}
