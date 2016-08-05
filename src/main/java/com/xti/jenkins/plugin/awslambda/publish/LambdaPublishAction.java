package com.xti.jenkins.plugin.awslambda.publish;

import hudson.model.ProminentProjectAction;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishAction implements ProminentProjectAction{
    private static final String URL_NAME = "console";

    private final String iconFileName;
    private final String bigIconFileName;
    private final String displayName;

    public LambdaPublishAction(String functionVersion, String functionAlias, Boolean success) {
        if(success){
            iconFileName = "/plugin/aws-lambda/images/Lambda_24.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48.png";
            displayName = "Lambda alias " + functionAlias + " points to version " + functionVersion;
        }else {
            iconFileName = "/plugin/aws-lambda/images/Lambda_24_grey.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48_grey.png";
            displayName = "Unable to point alias " + functionAlias + " to version " + functionVersion;
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