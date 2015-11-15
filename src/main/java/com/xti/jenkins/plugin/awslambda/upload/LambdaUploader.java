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

import com.amazonaws.services.lambda.model.FunctionCode;
import com.xti.jenkins.plugin.awslambda.service.*;

import java.io.IOException;

public class LambdaUploader {
    private LambdaDeployService lambda;
    private WorkSpaceZipper zipper;
    private JenkinsLogger logger;

    public LambdaUploader(LambdaDeployService lambda, WorkSpaceZipper zipper, JenkinsLogger logger) throws IOException, InterruptedException {
        this.lambda = lambda;
        this.zipper = zipper;
        this.logger = logger;
    }

    public DeployResult upload(DeployConfig config) throws IOException, InterruptedException {
        logger.log("%nStarting lambda deployment procedure");

        FunctionCode functionCode = lambda.getFunctionCode(config.getArtifactLocation(), zipper);
        return lambda.deployLambda(config, functionCode, UpdateModeValue.fromString(config.getUpdateMode()));
    }

    public AliasResult createAlias(AliasConfig aliasConfig) {
        logger.log("%nStarting lambda create alias procedure");
        return lambda.createAlias(aliasConfig);
    }
}
