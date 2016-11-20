package com.xti.jenkins.plugin.awslambda.util;

import hudson.EnvVars;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExpansionUtilsTest {

    @Test
    public void expansionTest(){
        EnvVars envVars = new EnvVars();
        envVars.put("ENV_ID", "ID");
        assertEquals("ID", ExpansionUtils.expand("${ENV_ID}", envVars));
        assertEquals("ID", ExpansionUtils.expand("$ENV_ID", envVars));
    }

}