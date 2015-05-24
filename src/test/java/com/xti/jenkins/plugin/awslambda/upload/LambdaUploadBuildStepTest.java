package com.xti.jenkins.plugin.awslambda.upload;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertEquals;

public class LambdaUploadBuildStepTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Ignore
    public void testHtml() throws Exception {
        LambdaUploadBuildStepVariables variables = new LambdaUploadBuildStepVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "ziplocation", "description", "function", "handler", 1024, "role", "nodejs", 30, "full");

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaUploadBuildStep before = new LambdaUploadBuildStep(variables);
        p.getBuildersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        LambdaUploadBuildStep after = p.getBuildersList().get(LambdaUploadBuildStep.class);

        assertEquals(before, after);
    }

}