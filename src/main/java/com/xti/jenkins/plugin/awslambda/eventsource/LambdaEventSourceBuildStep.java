package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.callable.EventSourceCallable;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
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

/**
 * Created by anthonyikeda on 25/11/2015.
 *
 */
public class LambdaEventSourceBuildStep extends Builder implements BuildStep {

    private LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables;

    @DataBoundConstructor
    public LambdaEventSourceBuildStep(LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables) {
        this.lambdaEventSourceBuildStepVariables = lambdaEventSourceBuildStepVariables;
    }

    public LambdaEventSourceBuildStepVariables getLambdaEventSourceBuildStepVariables() {
        return this.lambdaEventSourceBuildStepVariables;
    }

    public void setLambdaEventSourceBuildStepVariables(LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables) {
        this.lambdaEventSourceBuildStepVariables = lambdaEventSourceBuildStepVariables;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        return perform(lambdaEventSourceBuildStepVariables, build, launcher, listener);
    }

    public boolean perform(LambdaEventSourceBuildStepVariables lambdaEventSourceBuildStepVariables, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        try {
            LambdaEventSourceBuildStepVariables executionVariables = lambdaEventSourceBuildStepVariables.getClone();
            executionVariables.expandVariables(build.getEnvironment(listener));
            EventSourceConfig eventSourceConfig = executionVariables.getEventSourceConfig();
            LambdaClientConfig clientConfig = executionVariables.getLambdaClientConfig();

            EventSourceCallable eventSourceCallable = new EventSourceCallable(listener, eventSourceConfig, clientConfig);

            Boolean lambdaSuccess = launcher.getChannel().call(eventSourceCallable);

            if(!lambdaSuccess){
                build.setResult(Result.FAILURE);
            }

            build.addAction(new LambdaEventSourceAction(executionVariables.getFunctionName(), lambdaSuccess));
            return true;
        } catch(Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public BuildStepDescriptor getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

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
            return "AWS Lambda EventSource Mapper";
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
