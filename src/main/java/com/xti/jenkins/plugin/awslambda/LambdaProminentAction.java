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

import hudson.model.ProminentProjectAction;

public class LambdaProminentAction implements ProminentProjectAction {

    //Nothing to show, go to console for now
    private static final String URL_NAME = "console";

    private final String iconFileName;
    private final String bigIconFileName;
    private final String displayName;


    public LambdaProminentAction(String functionName, Boolean conforms) {
        if(conforms){
            iconFileName = "/plugin/aws-lambda/images/Lambda_24.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48.png";
            displayName = "Uploaded Lambda: " + functionName;
        }else {
            iconFileName = "/plugin/aws-lambda/images/Lambda_24_grey.png";
            bigIconFileName = "/plugin/aws-lambda/images/Lambda_48_grey.png";
            displayName = "Lambda Failure: " + functionName;
        }
    }

    @Override
    public String getIconFileName() {
        return iconFileName;
    }

    public String getBigIconFileName() {
        return bigIconFileName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getUrlName() {
        return URL_NAME;
    }
}
