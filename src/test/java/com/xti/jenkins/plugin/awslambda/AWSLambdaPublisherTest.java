package com.xti.jenkins.plugin.awslambda;

import hudson.model.FreeStyleProject;
import hudson.util.Secret;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AWSLambdaPublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Ignore
    public void testHtml() throws Exception {
        LambdaVariables variables = new LambdaVariables("accessKeyId", Secret.fromString("secretKey"), "eu-west-1", "ziplocation", "description", "function", "handler", 1024, "role", "nodejs", 30, true, "full");
        List<LambdaVariables> variablesList = new ArrayList<LambdaVariables>();
        variablesList.add(variables);

        FreeStyleProject p = j.createFreeStyleProject();
        AWSLambdaPublisher before = new AWSLambdaPublisher(variablesList);
        p.getPublishersList().add(before);

        j.submit(j.createWebClient().getPage(p,"configure").getFormByName("config"));

        AWSLambdaPublisher after = p.getPublishersList().get(AWSLambdaPublisher.class);

        assertEquals(before, after);
    }

}