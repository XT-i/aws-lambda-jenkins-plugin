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

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.IdCredentials;
import com.xti.jenkins.plugin.awslambda.callable.DeployCallable;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

public class LambdaUploadBuildStep extends Builder implements SimpleBuildStep{

    private LambdaUploadBuildStepVariables lambdaUploadBuildStepVariables;

    @DataBoundConstructor
    public LambdaUploadBuildStep(LambdaUploadBuildStepVariables lambdaUploadBuildStepVariables) {
        this.lambdaUploadBuildStepVariables = lambdaUploadBuildStepVariables;
    }

    public LambdaUploadBuildStepVariables getLambdaUploadBuildStepVariables() {
        return lambdaUploadBuildStepVariables;
    }

    public void setLambdaUploadBuildStepVariables(LambdaUploadBuildStepVariables LambdaUploadBuildStepVariables) {
        this.lambdaUploadBuildStepVariables = LambdaUploadBuildStepVariables;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        IdCredentials cred = CredentialsProvider.findCredentialById(
                lambdaUploadBuildStepVariables.getCredentialsId(),
                IdCredentials.class,
                run);
        StringCredentials c = (StringCredentials)cred;
        lambdaUploadBuildStepVariables.setAwsAccessKeyId(c.getId());
        lambdaUploadBuildStepVariables.setAwsSecretKey(c.getSecret().getPlainText());
        perform(lambdaUploadBuildStepVariables, run, workspace, launcher, listener);
    }

    public void perform(LambdaUploadBuildStepVariables lambdaUploadBuildStepVariables,  Run<?, ?>  run, FilePath workspace, Launcher launcher, TaskListener listener) {

        try {
            LambdaUploadBuildStepVariables executionVariables = lambdaUploadBuildStepVariables.getClone();
            executionVariables.expandVariables(run.getEnvironment(listener));
            DeployConfig deployConfig = executionVariables.getUploadConfig();
            LambdaClientConfig lambdaClientConfig = executionVariables.getLambdaClientConfig();

            DeployCallable deployCallable = new DeployCallable(listener, workspace, deployConfig, lambdaClientConfig);
            Boolean lambdaSuccess = launcher.getChannel().call(deployCallable);

            if (!lambdaSuccess) {
                run.setResult(Result.FAILURE);
            }
            run.addAction(new LambdaUploadAction(executionVariables.getFunctionName(), lambdaSuccess));
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    @Symbol("deployLambda")
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
            return "AWS Lambda deployment";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            save();

            return super.configure(req,formData);
        }
    }
}