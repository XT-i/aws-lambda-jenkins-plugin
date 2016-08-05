package com.xti.jenkins.plugin.awslambda;

import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

public abstract class AWSLambdaDescriptor<T extends Describable<T>> extends Descriptor<T> {

    //awsAccessKeyId field
    public FormValidation doCheckAwsAccessKeyId(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Access Key Id.");
        } else {
            return FormValidation.ok();
        }
    }

    //awsSecretKey field
    public FormValidation doCheckAwsSecretKey(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Secret Id.");
        } else {
            return FormValidation.ok();
        }
    }

    //awsRegion field
    public FormValidation doCheckAwsRegion(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Region.");
        } else {
            return FormValidation.ok();
        }
    }

    //functionName field
    public FormValidation doCheckFunctionName(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Lambda function name.");
        } else {
            return FormValidation.ok();
        }
    }

    //functionARN field
    public FormValidation doCheckFunctionARN(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Lambda function ARN.");
        } else {
            return FormValidation.ok();
        }
    }

    //functionAlias field
    public FormValidation doCheckFunctionAlias(@QueryParameter String value) {
        if(StringUtils.isEmpty(value)){
            return FormValidation.error("Please fill in AWS Lambda alias name.");
        } else {
            return FormValidation.ok();
        }
    }
}
