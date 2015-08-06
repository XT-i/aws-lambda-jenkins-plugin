package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;

public class LambdaClientConfig {
    private AWSLambdaClient client;

    public LambdaClientConfig(String region){
        client = new AWSLambdaClient(new DefaultAWSCredentialsProviderChain());
        client.setRegion(Region.getRegion(Regions.fromName(region)));
    }

    public LambdaClientConfig(String accessKeyId, String secretKey, String region) {
        client = new AWSLambdaClient(new BasicAWSCredentials(accessKeyId, secretKey));
        client.setRegion(Region.getRegion(Regions.fromName(region)));
    }

    public AWSLambdaClient getClient() {
        return client;
    }
}
