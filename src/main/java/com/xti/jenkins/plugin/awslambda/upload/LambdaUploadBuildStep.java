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

import com.xti.jenkins.plugin.awslambda.LambdaProminentAction;
import com.xti.jenkins.plugin.awslambda.LambdaVariables;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStep;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class LambdaUploadBuildStep extends Builder implements BuildStep{

    private LambdaVariables lambdaVariables;

    @DataBoundConstructor
    public LambdaUploadBuildStep(LambdaVariables lambdaVariables) {
        this.lambdaVariables = lambdaVariables;
    }

    public LambdaVariables getLambdaVariables() {
        return lambdaVariables;
    }

    public void setLambdaVariables(LambdaVariables lambdaVariables) {
        this.lambdaVariables = lambdaVariables;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return perform(lambdaVariables, build, launcher, listener);
    }

    public boolean perform(LambdaVariables lambdaVariables,AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try {
            LambdaVariables executionVariables = lambdaVariables.getClone();

            executionVariables.expandVariables(build.getEnvironment(listener));
            LambdaUploader lambdaUploader = new LambdaUploader(executionVariables, build, listener);

            Boolean lambdaSuccess = lambdaUploader.upload();
            if(!lambdaSuccess){
                build.setResult(Result.FAILURE);
            }
            build.addAction(new LambdaProminentAction(executionVariables.getFunctionName(), lambdaSuccess));
            return true;
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @SuppressWarnings("unchecked")
    @Override
    public BuildStepDescriptor getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }



    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }


        @SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Deploy into Lambda";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            save();

            return super.configure(req,formData);
        }
    }
}
