package com.xti.jenkins.plugin.awslambda.invoke;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LambdaInvokePublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testHtml() throws Exception {
        List<JsonParameterVariables> jsonParameterVariables = new ArrayList<JsonParameterVariables>();
        jsonParameterVariables.add(new JsonParameterVariables("KEY", "value"));
        LambdaInvokeVariables variables = new LambdaInvokeVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "function", "payload", true, true, jsonParameterVariables);
        List<LambdaInvokeVariables> variablesList = new ArrayList<LambdaInvokeVariables>();
        variablesList.add(variables);

        FreeStyleProject p = j.createFreeStyleProject();
        LambdaInvokePublisher before = new LambdaInvokePublisher(variablesList);
        p.getPublishersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        LambdaInvokePublisher after = p.getPublishersList().get(LambdaInvokePublisher.class);

        assertEquals(before, after);
    }
}