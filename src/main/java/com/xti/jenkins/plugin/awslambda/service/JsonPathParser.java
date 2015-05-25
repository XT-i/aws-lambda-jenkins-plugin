package com.xti.jenkins.plugin.awslambda.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.xti.jenkins.plugin.awslambda.invoke.JsonParameter;
import com.xti.jenkins.plugin.awslambda.util.LogUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonPathParser {
    private List<JsonParameter> jsonParameters;
    private JenkinsLogger logger;


    public JsonPathParser(List<JsonParameter> jsonParameters, JenkinsLogger logger) {
        this.jsonParameters = jsonParameters;
        this.logger = logger;
    }

    public Map<String, String> parse(String payload){
        Map<String, String> result = new HashMap<String, String>();
        if(StringUtils.isNotEmpty(payload)) {
            DocumentContext documentContext = JsonPath.parse(payload);

            for (JsonParameter jsonParameter : jsonParameters) {
                if (StringUtils.isEmpty(jsonParameter.getJsonPath())) {
                    result.put(jsonParameter.getEnvVarName(), payload);
                } else {
                    try {
                        result.put(jsonParameter.getEnvVarName(), documentContext.read(jsonParameter.getJsonPath()).toString());
                    } catch (JsonPathException jpe) {
                        logger.log("Error while parsing path %s for environment variable %s.%nStacktrace:%n%s%n",
                                jsonParameter.getJsonPath(), jsonParameter.getEnvVarName(), LogUtils.getStackTrace(jpe));
                    }
                }
            }
        }

        return result;
    }
}
