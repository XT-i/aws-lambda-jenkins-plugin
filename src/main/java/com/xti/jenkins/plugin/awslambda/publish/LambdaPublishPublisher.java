package com.xti.jenkins.plugin.awslambda.publish;

import com.xti.jenkins.plugin.awslambda.publish.LambdaPublishServiceResponse;
import com.xti.jenkins.plugin.awslambda.callable.PublishCallable;
import com.xti.jenkins.plugin.awslambda.util.LambdaClientConfig;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
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
 * Project: aws-lambda
 * Created by Magnus Sulland on 26/07/2016.
 */
public class LambdaPublishPublisher extends Notifier {

    List<LambdaPublishVariables> lambdaPublishVariablesList = new ArrayList<>();

    @DataBoundConstructor
    public LambdaPublishPublisher(List<LambdaPublishVariables> lambdaPublishVariablesList) {
        this.lambdaPublishVariablesList = lambdaPublishVariablesList;
    }

    public List<LambdaPublishVariables> getLambdaPublishVariablesList() {
        return lambdaPublishVariablesList;
    }

    public void setLambdaPublishVariablesList(List<LambdaPublishVariables> lambdaPublishVariablesList) {
        this.lambdaPublishVariablesList = lambdaPublishVariablesList;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        boolean returnValue = true;

        if(lambdaPublishVariablesList != null) {
            for(LambdaPublishVariables variables : lambdaPublishVariablesList) {
                returnValue = returnValue && perform(variables, build, launcher, listener);
            }
        }
        return returnValue;
    }

    public boolean perform(LambdaPublishVariables variables, AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) {
        try {
            LambdaPublishVariables executionVariables = variables.getClone();
            executionVariables.expandVariables(build.getEnvironment(listener));
            PublishConfig publishConfig = executionVariables.getPublishConfig();
            LambdaClientConfig clientConfig = executionVariables.getLambdaClientConfig();

            PublishCallable publishCallable = new PublishCallable(listener, publishConfig, clientConfig);

            LambdaPublishServiceResponse lambdaRepsonse = launcher.getChannel().call(publishCallable);

            if(!lambdaRepsonse.getSuccess()){
                build.setResult(Result.FAILURE);
            }

            build.addAction(new LambdaPublishAction(lambdaRepsonse.getFunctionVersion(), lambdaRepsonse.getFunctionAlias(), lambdaRepsonse.getSuccess()));
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
        return (LambdaPublishPublisher.DescriptorImpl)super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

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