package com.xti.jenkins.plugin.awslambda.invoke;

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

import com.xti.jenkins.plugin.awslambda.service.*;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.IOException;
import java.util.Map;

public class LambdaInvoker {
    private JenkinsLogger logger;
    private InvokeConfig config;
    private LambdaInvokeService lambda;
    private JsonPathParser jsonPathParser;

    public LambdaInvoker(InvokeConfig config, AbstractBuild<?, ?> build, BuildListener listener) throws IOException, InterruptedException {
        this.config = config;
        logger = new JenkinsLogger(listener.getLogger());
        LambdaClientConfig lambdaClientConfig = new LambdaClientConfig(config.getAwsAccessKeyId(), config.getAwsSecretKey(), config.getAwsRegion());
        lambda = new LambdaInvokeService(lambdaClientConfig.getClient(), logger);
        jsonPathParser = new JsonPathParser(config.getJsonParameters(), logger);
    }

    public LambdaInvocationResult invoke() throws IOException, InterruptedException {
        logger.log("%nStarting lambda invocation.");

        String output = lambda.invokeLambdaFunction(config);

        Map<String, String> injectables = jsonPathParser.parse(output);

        return new LambdaInvocationResult(true, injectables);
    }
}
