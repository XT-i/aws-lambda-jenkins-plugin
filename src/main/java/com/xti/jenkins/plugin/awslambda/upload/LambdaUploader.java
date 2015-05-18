package com.xti.jenkins.plugin.awslambda.upload;

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

import com.xti.jenkins.plugin.awslambda.LambdaVariables;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaClientConfig;
import com.xti.jenkins.plugin.awslambda.service.LambdaService;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import org.apache.commons.lang.StringUtils;

import java.io.*;

public class LambdaUploader {
    private JenkinsLogger logger;
    private LambdaVariables config;
    private LambdaService lambda;
    private FilePath artifactLocation = null;

    public LambdaUploader(LambdaVariables config, AbstractBuild<?, ?> build, BuildListener listener) throws IOException, InterruptedException {
        this.config = config;
        logger = new JenkinsLogger(listener.getLogger());
        LambdaClientConfig lambdaClientConfig = new LambdaClientConfig(config.getAwsAccessKeyId(), config.getAwsSecretKey(), config.getAwsRegion());
        lambda = new LambdaService(lambdaClientConfig.getClient(), logger);
        if(StringUtils.isNotEmpty(config.getArtifactLocation())) {
            artifactLocation = new FilePath(build.getWorkspace(), config.getArtifactLocation());
        }
        build.getEnvironment(listener);
    }

    public Boolean upload() throws IOException, InterruptedException {
        logger.log("%nStarting lambda upload procedure");
        File zipFile = null;
        if(artifactLocation != null){
            zipFile = getArtifactFile(artifactLocation);
        }
        return lambda.processLambdaDeployment(config, zipFile, UpdateModeValue.fromString(config.getUpdateMode()));
    }

    private File getArtifactFile(FilePath artifactLocation) throws IOException, InterruptedException {
        File resultFile = File.createTempFile("awslambda-", ".zip");

        if (!artifactLocation.isDirectory()) {
            logger.log("Copying zip file");
            artifactLocation.copyTo(new FileOutputStream(resultFile));
        } else {
            logger.log("Zipping folder ..., copying zip file");
            artifactLocation.zip(new FileOutputStream(resultFile));
        }

        logger.log("File Name: %s%nAbsolute Path: %s%nFile Size: %d", resultFile.getName(), resultFile.getAbsolutePath(), resultFile.length());
        return resultFile;
    }

}
