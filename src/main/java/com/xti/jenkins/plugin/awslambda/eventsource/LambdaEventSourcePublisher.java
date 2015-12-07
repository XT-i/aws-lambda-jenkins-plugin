package com.xti.jenkins.plugin.awslambda.eventsource;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaDeployService;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anthonyikeda on 25/11/2015.
 */
public class LambdaEventSourcePublisher extends Notifier {

    List<LambdaEventSourceVariables> lambdaEventSourceVariablesList = new ArrayList<>();

    @DataBoundConstructor
    public LambdaEventSourcePublisher(List<LambdaEventSourceVariables> lambdaEventSourceVariablesList) {
        this.lambdaEventSourceVariablesList = lambdaEventSourceVariablesList;
    }

    public List<LambdaEventSourceVariables> getLambdaEventSourceVariablesList() {
        return lambdaEventSourceVariablesList;
    }

    public void setLambdaEventSourceVariablesList(List<LambdaEventSourceVariables> lambdaEventSourceVariablesList) {
        this.lambdaEventSourceVariablesList = lambdaEventSourceVariablesList;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) {
        boolean returnValue = true;

        if(lambdaEventSourceVariablesList != null) {
            for(LambdaEventSourceVariables variables : lambdaEventSourceVariablesList) {
                returnValue = returnValue && perform(variables, build, launcher, listener);
            }
        }
        return returnValue;
    }

    public boolean perform(LambdaEventSourceVariables variables, AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) {
        if(variables.getSuccessOnly() && build.getResult().isWorseThan(Result.SUCCESS)) {
            listener.getLogger().println("Build not successful, not applying event source to Lambda function: " + variables.getFunctionName());
            return true;
        } else if(!variables.getSuccessOnly() && build.getResult().isWorseThan(Result.UNSTABLE)) {
            listener.getLogger().println("Build failed, not applying event source to Lambda function: " + variables.getFunctionName());
            return true;
        }

        try {
            LambdaEventSourceVariables executionVariables = variables.getClone();
            executionVariables.expandVariables(build.getEnvironment(listener));
            JenkinsLogger logger = new JenkinsLogger(listener.getLogger());
            LambdaClientConfig clientConfig = executionVariables.getLambdaClientConfig();
            LambdaDeployService service = new LambdaDeployService(clientConfig.getClient(), logger);
            EventSourceConfig eventSourceConfig = executionVariables.getEventSourceConfig();
            EventSourceBuilder builder = new EventSourceBuilder(service, logger);

            Boolean lambdaSuccess = builder.createEventSource(eventSourceConfig);

            if(!lambdaSuccess){
                build.setResult(Result.FAILURE);
            }

            build.addAction(new LambdaEventSourceAction(executionVariables.getFunctionName(), lambdaSuccess));
            return true;
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public DescriptorImpl() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "AWS Lambda Event Source Mapper";
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            req.bindJSON(this, formData);

            save();

            return super.configure(req,formData);
        }
    }
}
