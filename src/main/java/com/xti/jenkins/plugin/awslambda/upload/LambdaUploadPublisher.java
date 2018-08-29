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
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadAction;
import com.xti.jenkins.plugin.awslambda.upload.DeployConfig;
import com.xti.jenkins.plugin.awslambda.upload.LambdaUploadVariables;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

public class LambdaUploadPublisher extends Notifier{

    List<LambdaUploadVariables> lambdaVariablesList = new ArrayList<LambdaUploadVariables>();

    @DataBoundConstructor
    public LambdaUploadPublisher(List<LambdaUploadVariables> lambdaVariablesList) {
        this.lambdaVariablesList = lambdaVariablesList;
    }

    public List<LambdaUploadVariables> getLambdaVariablesList() {
        return lambdaVariablesList;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) {
        boolean returnValue = true;
        EnvVars environment;
        try{environment = build.getEnvironment(listener);}
        catch(Exception e){
            e.printStackTrace();
        }

        if (lambdaVariablesList != null){
            for (LambdaUploadVariables lambdaVariables : lambdaVariablesList) {
                IdCredentials cred = CredentialsProvider.findCredentialById(
                        lambdaVariables.getCredentialsId(),
                        IdCredentials.class,
                        build);
                StringCredentials c = (StringCredentials)cred;
                lambdaVariables.setAwsAccessKeyId(c.getId());
                lambdaVariables.setAwsSecretKey(c.getSecret().getPlainText());

                returnValue = returnValue && perform(lambdaVariables, build, launcher, listener);
            }
        }

        return returnValue;
    }

    public boolean perform(LambdaUploadVariables lambdaVariables,AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        if (lambdaVariables.getSuccessOnly() && build.getResult().isWorseThan(Result.SUCCESS)) {
            listener.getLogger().println("Build not successful, not uploading Lambda function: " + lambdaVariables.getFunctionName());
            return true;
        } else if (!lambdaVariables.getSuccessOnly() && build.getResult().isWorseThan(Result.UNSTABLE)) {
            listener.getLogger().println("Build failed, not uploading Lambda function: " + lambdaVariables.getFunctionName());
            return true;
        }

        try {
            LambdaUploadVariables executionVariables = lambdaVariables.getClone();
            executionVariables.expandVariables(build.getEnvironment(listener));
            FilePath localWorkspace = build.getWorkspace();
            DeployConfig deployConfig = executionVariables.getUploadConfig();
            LambdaClientConfig lambdaClientConfig = executionVariables.getLambdaClientConfig();

            DeployCallable deployCallable = new DeployCallable(listener, localWorkspace, deployConfig, lambdaClientConfig);
            Boolean lambdaSuccess = launcher.getChannel().call(deployCallable);

            if (!lambdaSuccess) {
                build.setResult(Result.FAILURE);
            }
            build.addAction(new LambdaUploadAction(executionVariables.getFunctionName(), lambdaSuccess));

            return true;

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
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        static{
            Items.XSTREAM2.addCompatibilityAlias("com.xti.jenkins.plugin.awslambda.AWSLambdaPublisher", com.xti.jenkins.plugin.awslambda.upload.LambdaUploadPublisher.class);
        }

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