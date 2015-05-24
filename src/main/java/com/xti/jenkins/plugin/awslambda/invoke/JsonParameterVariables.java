package com.xti.jenkins.plugin.awslambda.invoke;

/*
 * #%L
 * AWS Lambda Upload Plugin
 * %%
 * Copyright (C) 2015 XT-i
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class JsonParameterVariables extends AbstractDescribableImpl<JsonParameterVariables> {
    private String envVarName;
    private String jsonPath;

    @DataBoundConstructor
    public JsonParameterVariables(String envVarName, String jsonPath) {
        this.envVarName = envVarName;
        this.jsonPath = jsonPath;
    }

    public String getEnvVarName() {
        return envVarName;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setEnvVarName(String envVarName) {
        this.envVarName = envVarName;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public JsonParameter getJsonParameter(){
        return new JsonParameter(envVarName, jsonPath);
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends Descriptor<JsonParameterVariables> {

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Environment Variable";
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonParameterVariables that = (JsonParameterVariables) o;

        if (envVarName != null ? !envVarName.equals(that.envVarName) : that.envVarName != null) return false;
        return !(jsonPath != null ? !jsonPath.equals(that.jsonPath) : that.jsonPath != null);

    }

    @Override
    public int hashCode() {
        int result = envVarName != null ? envVarName.hashCode() : 0;
        result = 31 * result + (jsonPath != null ? jsonPath.hashCode() : 0);
        return result;
    }
}
