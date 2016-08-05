package com.xti.jenkins.plugin.awslambda.publish;

import com.xti.jenkins.plugin.awslambda.callable.PublishCallable;
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
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishBuildStep extends Builder implements SimpleBuildStep {
    private LambdaPublishBuildStepVariables lambdaPublishBuildStepVariables;

    @DataBoundConstructor
    public LambdaPublishBuildStep(LambdaPublishBuildStepVariables lambdaPublishBuildStepVariables) {
        this.lambdaPublishBuildStepVariables = lambdaPublishBuildStepVariables;
    }

    public LambdaPublishBuildStepVariables getLambdaPublishBuildStepVariables() {
        return this.lambdaPublishBuildStepVariables;
    }

    public void setLambdaPublishBuildStepVariables(LambdaPublishBuildStepVariables lambdaPublishBuildStepVariables){
        this.lambdaPublishBuildStepVariables = lambdaPublishBuildStepVariables;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        perform(lambdaPublishBuildStepVariables, run, launcher, listener);
    }

    public void perform(LambdaPublishBuildStepVariables lambdaPublishBuildStepVariables, Run<?, ?> run, Launcher launcher, TaskListener listener) {
        try {
            LambdaPublishBuildStepVariables executionVariables = lambdaPublishBuildStepVariables.getClone();
            executionVariables.expandVariables(run.getEnvironment(listener));
            PublishConfig publishConfig = executionVariables.getPublishConfig();
            LambdaClientConfig clientConfig = executionVariables.getLambdaClientConfig();

            PublishCallable publishCallable = new PublishCallable(listener, publishConfig, clientConfig);

            LambdaPublishServiceResponse lambdaSuccess = launcher.getChannel().call(publishCallable);

            if(!lambdaSuccess.getSuccess()){
                run.setResult(Result.FAILURE);
            }

            run.addAction(new LambdaPublishAction(lambdaSuccess.getFunctionVersion(), lambdaSuccess.getFunctionAlias(), lambdaSuccess.getSuccess()));
        } catch(Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @SuppressWarnings("unchecked")
    @Override
    public BuildStepDescriptor getDescriptor() {
        return (LambdaPublishBuildStep.DescriptorImpl)super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

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
            return "AWS Lambda publish new version and update alias";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            save();

            return super.configure(req,formData);
        }
    }
}