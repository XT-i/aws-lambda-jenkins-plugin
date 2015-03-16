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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.*;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

public class AWSLambdaPublisher extends Notifier{

    List<LambdaVariables> lambdaVariablesList = new ArrayList<LambdaVariables>();

    @DataBoundConstructor
    public AWSLambdaPublisher(List<LambdaVariables> lambdaVariablesList, boolean PublishUnstable) {
        this.lambdaVariablesList = lambdaVariablesList;
    }

    public List<LambdaVariables> getLambdaVariablesList() {
        return lambdaVariablesList;
    }

    public void setLambdaVariablesList(List<LambdaVariables> lambdaVariablesList) {
        this.lambdaVariablesList = lambdaVariablesList;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) {
        boolean returnValue = true;

        for (LambdaVariables lambdaVariables : lambdaVariablesList) {
            returnValue = returnValue && perform(lambdaVariables, build, launcher, listener);
        }

        return returnValue;
    }

    public boolean perform(LambdaVariables lambdaVariables,AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        if (lambdaVariables.getSuccessOnly() && build.getResult().isWorseThan(Result.SUCCESS)) {
            listener.getLogger().println("Build not successful, not uploading Lambda function: " + lambdaVariables.getFunctionName());
            return true;
        } else if (!lambdaVariables.getSuccessOnly() && build.getResult().isWorseThan(Result.UNSTABLE)) {
            listener.getLogger().println("Build failed, not uploading Lambda function: " + lambdaVariables.getFunctionName());
            return true;
        }
        try {
            lambdaVariables.expandVariables(build.getEnvironment(listener));
            LambdaUploader lambdaUploader = new LambdaUploader(lambdaVariables, build, listener);

            lambdaUploader.upload();
            build.addAction(new LambdaProminentAction(lambdaVariables.getFunctionName(), lambdaUploader.getLambdaResultConforms()));
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
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckTimeout(@QueryParameter String value) {
            try {
                Integer.parseInt(value);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error("Not a number");
            }
        }

        public FormValidation doCheckMemorySize(@QueryParameter String value) {
            try {
                Integer.parseInt(value);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error("Not a number");
            }
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
