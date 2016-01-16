package com.xti.jenkins.plugin.awslambda.eventsource;

import hudson.model.ProminentProjectAction;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public class LambdaEventSourceAction implements ProminentProjectAction {
    private static final String URL_NAME = "console";

    private final String iconFileName;
    private final String bigIconFileName;
    private final String displayName;

    public LambdaEventSourceAction(String functionName, Boolean success) {
        if(success){
            iconFileName = "/plugin/aws-lambda/images/Lambda_24.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48.png";
            displayName = "Lambda event sources created: " + functionName;
        }else {
            iconFileName = "/plugin/aws-lambda/images/Lambda_24_grey.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48_grey.png";
            displayName = "Lambda event sources creation failure: " + functionName;
        }
    }

    @Override
    public String getIconFileName() {
        return iconFileName;
    }

    public String getBigIconFileName() {
        return bigIconFileName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }
}
