package com.xti.jenkins.plugin.awslambda.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import hudson.ProxyConfiguration;

import java.io.Serializable;

public class LambdaClientConfig implements Serializable {
    private String region;
    private String accessKeyId;
    private String secretKey;
    private Boolean useDefaultAWSCredentials;
    private ProxyConfiguration proxyConfiguration;

    public LambdaClientConfig(String region, ProxyConfiguration proxyConfiguration){
        this.region = region;
        this.useDefaultAWSCredentials = true;
        this.proxyConfiguration = proxyConfiguration;
    }

    public LambdaClientConfig(String accessKeyId, String secretKey, String region, ProxyConfiguration proxyConfiguration) {
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretKey = secretKey;
        this.useDefaultAWSCredentials = false;
        this.proxyConfiguration = proxyConfiguration;
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
        /*
        Jenkins instance = Jenkins.getInstance();

        if (instance != null) {
            ProxyConfiguration proxy = instance.proxy;
        */
        ClientConfiguration config = new ClientConfiguration();

        if (proxyConfiguration != null) {
            config.setProxyHost(proxyConfiguration.name);
            config.setProxyPort(proxyConfiguration.port);
            if (proxyConfiguration.getUserName() != null) {
                config.setProxyUsername(proxyConfiguration.getUserName());
                config.setProxyPassword(proxyConfiguration.getPassword());
            }
            if (proxyConfiguration.noProxyHost != null) {
                config.setNonProxyHosts(proxyConfiguration.noProxyHost);
            }
        }

        return config;

    }
}
