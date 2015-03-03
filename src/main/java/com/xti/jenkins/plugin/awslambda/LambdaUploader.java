package com.xti.jenkins.plugin.awslambda;

/*
 * #%L
 * AWS Lambda Upload Plugin
 * %%
 * Copyright (C) 2015 XT-i
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.UploadFunctionRequest;
import com.amazonaws.services.lambda.model.UploadFunctionResult;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.*;

public class LambdaUploader {
    private final PrintStream logger;
    private final FilePath artifactLocation;

    private ResolvedLambdaVariables config;
    private AWSLambda lambda;
    private File artifact;
    private Boolean lambdaResultConforms = false;

    public LambdaUploader(ResolvedLambdaVariables config,
                          AbstractBuild<?, ?> build, BuildListener listener) throws IOException, InterruptedException {
        this.config = config;
        this.logger = listener.getLogger();

        this.artifactLocation = new FilePath(build.getWorkspace(), config.getArtifactLocation());
    }

    public void upload() throws IOException, InterruptedException {
        log("Starting lambda upload procedure");
        getArtifactFile(artifactLocation);
        setupLambdaClient();
        uploadLambdaFunction();
    }

    private void getArtifactFile(FilePath artifactLocation) throws IOException, InterruptedException {
        File resultFile = File.createTempFile("awslambda-", ".zip");

        if (!artifactLocation.isDirectory()) {
            log("Copying zip file");
            artifactLocation.copyTo(new FileOutputStream(resultFile));
        } else {
            log("Zipping folder ..., copying zip file");
            artifactLocation.zip(new FileOutputStream(resultFile));
        }

        log("File Name: %s%nAbsolute Path: %s%nFile Size: %d", resultFile.getName(), resultFile.getAbsolutePath(), resultFile.length());
        artifact = resultFile;
    }

    private void setupLambdaClient() {
        AWSCredentialsProvider credentials = new AWSCredentialsProviderChain(
                new StaticCredentialsProvider(
                        new BasicAWSCredentials(
                        config.getAwsAccessKeyId(),
                        config.getAwsSecretKey())
                )
        );
        Region region = Region.getRegion(Regions.fromName(config.getAwsRegion()));

        lambda = new AWSLambdaClient(credentials);
        lambda.setRegion(region);
        log("Lambda credentials set for region: %s", region);
    }

    private void uploadLambdaFunction() throws FileNotFoundException {
        UploadFunctionRequest uploadRequest = new UploadFunctionRequest()
                .withDescription(config.getDescription())
                .withFunctionName(config.getFunctionName())
                .withHandler(config.getHandler())
                .withMemorySize(config.getMemorySize())
                .withTimeout(config.getTimeout())
                .withMode(config.getMode())
                .withRole(config.getRole())
                .withRuntime(config.getRuntime())
                .withFunctionZip(new FileInputStream(artifact));
        log("%nLambda upload request:%n%s%n", uploadRequest.toString());

        UploadFunctionResult uploadFunctionResult = lambda.uploadFunction(uploadRequest);
        log("Lambda upload response:%n%s%n", uploadFunctionResult.toString());

        verifyUploadFunctionResult(uploadRequest, uploadFunctionResult);
    }

    private void verifyUploadFunctionResult(UploadFunctionRequest request, UploadFunctionResult result) {
        if(
                result.getFunctionName().equals(request.getFunctionName())
                && result.getHandler().equals(request.getHandler())
                && result.getMemorySize().equals(request.getMemorySize()) || request.getMemorySize() == null
                && result.getTimeout().equals(request.getTimeout()) || request.getTimeout() == null
                && result.getRuntime().equals(request.getRuntime())
                && result.getMode().equals(request.getMode())
                && result.getRole().equals(request.getRole())){
            log("Lambda function response conforms with request");
            lambdaResultConforms = true;
        }else {
            log("Warning: Lambda function response does not conform with request! " +
                    "%nCheck AWS console and plugin configuration");
            lambdaResultConforms = false;
        }
    }

    public Boolean getLambdaResultConforms() {
        return lambdaResultConforms;
    }

    void log(String mask, Object... args) {
        logger.println(String.format(mask, args));
    }
}
