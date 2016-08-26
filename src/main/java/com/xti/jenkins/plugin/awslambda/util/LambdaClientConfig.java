package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

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
            return new AWSLambdaClient(new DefaultAWSCredentialsProviderChain(), getClientConfiguration())
                    .withRegion(Region.getRegion(Regions.fromName(region)));
        } else {
            return new AWSLambdaClient(new BasicAWSCredentials(accessKeyId, secretKey), getClientConfiguration())
                    .withRegion(Region.getRegion(Regions.fromName(region)));
        }
    }

    private ClientConfiguration getClientConfiguration() {
        ClientConfiguration config = new ClientConfiguration();
        Jenkins instance = Jenkins.getInstance();

        if (instance != null) {
            ProxyConfiguration proxy = instance.proxy;

            if (proxy != null) {
                config.setProxyHost(proxy.name);
                config.setProxyPort(proxy.port);
                if (proxy.getUserName() != null) {
                    config.setProxyUsername(proxy.getUserName());
                    config.setProxyPassword(proxy.getPassword());
                }
                if (proxy.noProxyHost != null){
                    config.setNonProxyHosts(proxy.noProxyHost);
                }
            }
        }
        return config;
    }
}
