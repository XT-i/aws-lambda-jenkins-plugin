package com.xti.jenkins.plugin.awslambda.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.xti.jenkins.plugin.awslambda.invoke.JsonParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPathParser {
    List<JsonParameter> jsonParameters;

    public JsonPathParser(List<JsonParameter> jsonParameters) {
        this.jsonParameters = jsonParameters;
    }

    public Map<String, String> parse(String payload){
        Map<String, String> result = new HashMap<String, String>();
        DocumentContext documentContext = JsonPath.parse(payload);

        for (JsonParameter jsonParameter : jsonParameters) {
            result.put(jsonParameter.getEnvVarName(), documentContext.read(jsonParameter.getJsonPath()).toString());
        }

        return result;
    }
}
