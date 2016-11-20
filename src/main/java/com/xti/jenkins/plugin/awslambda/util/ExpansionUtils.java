package com.xti.jenkins.plugin.awslambda.util;

import hudson.EnvVars;
import hudson.Util;

public class ExpansionUtils {

    public static String expand(String value, EnvVars env) {
        if(value != null) {
            return Util.replaceMacro(value.trim(), env);
        } else {
            return null;
        }
    }
}
