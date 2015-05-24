package com.xti.jenkins.plugin.awslambda.invoke;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LambdaInvokeBuildStepTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Ignore
    @Test
    public void testHtml() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "value"));
        LambdaInvokeBuildStepVariables variables = new LambdaInvokeBuildStepVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, jsonParameterVariables);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaInvokeBuildStep before = new LambdaInvokeBuildStep(variables);
        p.getBuildersList().add(before);

        j.submit(j.createWebClient().getPage(p, "configure").getFormByName("config"));

        LambdaInvokeBuildStep after = p.getBuildersList().get(LambdaInvokeBuildStep.class);

        assertEquals(before, after);
    }
}