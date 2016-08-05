package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.*;
import com.xti.jenkins.plugin.awslambda.publish.LambdaPublishServiceResponse;
import com.xti.jenkins.plugin.awslambda.publish.PublishConfig;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishService {
    private AWSLambdaClient client;
    private JenkinsLogger logger;

    public LambdaPublishService(AWSLambdaClient client, JenkinsLogger logger) {
        this.client = client;
        this.logger = logger;
    }

    public LambdaPublishServiceResponse publishLambda(PublishConfig config){

        GetAliasRequest getAliasRequest = new GetAliasRequest()
                .withName(config.getFunctionAlias())
                .withFunctionName(config.getFunctionARN());

        logger.log("Lambda function alias existence check:%n%s%n%s%n", config.getFunctionARN(), config.getFunctionAlias());
        try {
            GetAliasResult functionResult = client.getAlias(getAliasRequest);
            logger.log("Lambda function alias exists:%n%s%n", functionResult.toString());
        } catch (ResourceNotFoundException rnfe) {
            return new LambdaPublishServiceResponse("", "", false);
        }

        logger.log("Publishing new version");

        PublishVersionRequest publishVersionRequest = new PublishVersionRequest().withFunctionName(config.getFunctionARN()).withDescription(config.getVersionDescription());
        PublishVersionResult publishVersionResult = client.publishVersion(publishVersionRequest);

        logger.log(publishVersionResult.toString());

        logger.log("Updating alias");

        UpdateAliasRequest updateAliasRequest = new UpdateAliasRequest().withName(config.getFunctionAlias());
        updateAliasRequest.setFunctionName(config.getFunctionARN());
        updateAliasRequest.setDescription(config.getVersionDescription());
        updateAliasRequest.setFunctionVersion(publishVersionResult.getVersion());

        UpdateAliasResult updateAliasResult = client.updateAlias(updateAliasRequest);

        logger.log(updateAliasResult.toString());

        return new LambdaPublishServiceResponse(publishVersionResult.getVersion(), updateAliasResult.getName(), true);
    }

}
