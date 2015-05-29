package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;

public class LambdaClientConfig {
    private AWSLambdaClient client;

    public LambdaClientConfig(String accessKeyId, String secretKey, String region) {
        client = new AWSLambdaClient(new BasicAWSCredentials(accessKeyId, secretKey));
        client.setRegion(Region.getRegion(Regions.fromName(region)));
    }

    public AWSLambdaClient getClient() {
        return client;
    }
}
