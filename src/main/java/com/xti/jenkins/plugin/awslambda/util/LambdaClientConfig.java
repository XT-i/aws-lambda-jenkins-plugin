package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;

import java.io.Serializable;

public class LambdaClientConfig implements Serializable {
    private String region;
    private String accessKeyId;
    private String secretKey;
    private Boolean useDefaultAWSCredentials;

    public LambdaClientConfig(String region){
        this.region = region;
        useDefaultAWSCredentials = true;
    }

    public LambdaClientConfig(String accessKeyId, String secretKey, String region) {
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
        useDefaultAWSCredentials = false;
    }

    public AWSLambdaClient getClient() {
        if(useDefaultAWSCredentials){
            return new AWSLambdaClient(new DefaultAWSCredentialsProviderChain())
                    .withRegion(Region.getRegion(Regions.fromName(region)));
        } else {
            return new AWSLambdaClient(new BasicAWSCredentials(accessKeyId, secretKey))
                    .withRegion(Region.getRegion(Regions.fromName(region)));
        }
    }
}
