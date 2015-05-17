package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;

public class LambdaClientConfig {
    private AWSLambdaClient client;

    public LambdaClientConfig(String accessKeyId, String secretKey, String region) {
        AWSCredentialsProvider credentials = new AWSCredentialsProviderChain(new StaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretKey)));
        client = new AWSLambdaClient(credentials);
        client.setRegion(Region.getRegion(Regions.fromName(region)));
    }

    public AWSLambdaClient getClient() {
        return client;
    }
}
