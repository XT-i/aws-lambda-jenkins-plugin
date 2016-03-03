package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.callable.EventSourceCallable;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStep;
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
 * Created by anthonyikeda on 25/11/2015.
 *
 */
public class LambdaEventSourceBuildStep extends Builder implements SimpleBuildStep {

    private LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables;

    @DataBoundConstructor
    public LambdaEventSourceBuildStep(LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables) {
        this.lambdaEventSourceBuildStepVariables = lambdaEventSourceBuildStepVariables;
    }

    public LambdaEventSourceBuildStepVariables getLambdaEventSourceBuildStepVariables() {
        return this.lambdaEventSourceBuildStepVariables;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        perform(lambdaEventSourceBuildStepVariables, run, launcher, listener);
    }

    public void perform(LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables, Run<?, ?> run, Launcher launcher, TaskListener listener) {
        try {
            LambdaEventSourceBuildStepVariables executionVariables = lambdaEventSourceBuildStepVariables.getClone();
            executionVariables.expandVariables(run.getEnvironment(listener));
            EventSourceConfig eventSourceConfig = executionVariables.getEventSourceConfig();
            LambdaClientConfig clientConfig = executionVariables.getLambdaClientConfig();

            EventSourceCallable eventSourceCallable = new EventSourceCallable(listener, eventSourceConfig, clientConfig);

            Boolean lambdaSuccess = launcher.getChannel().call(eventSourceCallable);

            if(!lambdaSuccess){
                run.setResult(Result.FAILURE);
            }

            run.addAction(new LambdaEventSourceAction(executionVariables.getFunctionName(), lambdaSuccess));
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
            return "AWS Lambda eventsource mapping";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            save();

            return super.configure(req,formData);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LambdaEventSourceBuildStep)) return false;

        LambdaEventSourceBuildStep that = (LambdaEventSourceBuildStep) o;

        return !(lambdaEventSourceBuildStepVariables != null ? !lambdaEventSourceBuildStepVariables.equals(that.lambdaEventSourceBuildStepVariables) : that.lambdaEventSourceBuildStepVariables != null);

    }

    @Override
    public int hashCode() {
        return lambdaEventSourceBuildStepVariables != null ? lambdaEventSourceBuildStepVariables.hashCode() : 0;
    }
}
