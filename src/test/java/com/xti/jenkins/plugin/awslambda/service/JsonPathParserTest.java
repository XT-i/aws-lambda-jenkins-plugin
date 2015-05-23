package com.xti.jenkins.plugin.awslambda.service;

import com.xti.jenkins.plugin.awslambda.invoke.JsonParameter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonPathParserTest {

    List<JsonParameter> jsonParameters;

    @Before
    public void setUp() throws Exception {
        jsonParameters = new ArrayList<JsonParameter>();
    }

    @Test
    public void testSimpleParse() throws Exception {
        jsonParameters.add(lambdaKey1);
        jsonParameters.add(lambdaKey2);
        jsonParameters.add(keyPayload);
        jsonParameters.add(keyPayloadNull);
        JsonPathParser jsonPathParser = new JsonPathParser(jsonParameters, new JenkinsLogger(System.out));
        Map<String, String> parseResult = jsonPathParser.parse(simplePayload);

        assertEquals(4, parseResult.size());
        assertEquals("value1", parseResult.get(lambdaKey1.getEnvVarName()));
        assertEquals("value2", parseResult.get(lambdaKey2.getEnvVarName()));
        assertEquals(simplePayload, parseResult.get(keyPayload.getEnvVarName()));
        assertEquals(simplePayload, parseResult.get(keyPayloadNull.getEnvVarName()));
    }

    @Test
    public void testSimpleParseInvalid() throws Exception {
        jsonParameters.add(keyInvalid);
        jsonParameters.add(lambdaKey4);
        JsonPathParser jsonPathParser = new JsonPathParser(jsonParameters, new JenkinsLogger(System.out));
        Map<String, String> parseResult = jsonPathParser.parse(simplePayload);

        assertEquals(0, parseResult.size());
    }

    final JsonParameter lambdaKey1 = new JsonParameter("LAMBDA_KEY_1", "$.key1");
    final JsonParameter lambdaKey2 = new JsonParameter("LAMBDA_KEY_2", "key2");
    final JsonParameter keyPayload = new JsonParameter("LAMBDA_KEYS", "");
    final JsonParameter keyPayloadNull = new JsonParameter("LAMBDA_KEYS_NULL", null);
    final JsonParameter keyInvalid = new JsonParameter("LAMBDA_KEYS_INVALID_DOT", "$.");
    final JsonParameter lambdaKey4 = new JsonParameter("LAMBDA_KEYS_INVALID_KEY", "$.key4");

    final String simplePayload =
            "{\n" +
                    "  \"key1\": \"value1\",\n" +
                    "  \"key2\": \"value2\",\n" +
                    "  \"key3\": \"value3\"\n" +
                    "}";


    @Test
    public void testComplexParse() throws Exception {
        jsonParameters.add(complexDeep);
        jsonParameters.add(complexFilter);
        JsonPathParser jsonPathParser = new JsonPathParser(jsonParameters, new JenkinsLogger(System.out));
        Map<String, String> parseResult = jsonPathParser.parse(complexPayload);

        assertEquals(2, parseResult.size());
        assertEquals("attached", parseResult.get(complexDeep.getEnvVarName()));
        assertEquals("[\"\\/dev\\/sdf\"]", parseResult.get(complexFilter.getEnvVarName()));
    }

    final JsonParameter complexDeep = new JsonParameter("LAMBDA_COMPLEX_1", "BlockDeviceMappings[0].Ebs.Status");
    final JsonParameter complexFilter = new JsonParameter("LAMBDA_COMPLEX_FIRSTFALSE", "$.BlockDeviceMappings[?(@.Ebs.DeleteOnTermination == 'false')].DeviceName");


    final String complexPayload =
            "{\n" +
                    "    \"InstanceId\": \"i-5203422c\",\n" +
                    "    \"BlockDeviceMappings\": [\n" +
                    "        {\n" +
                    "            \"DeviceName\": \"/dev/sda1\",\n" +
                    "            \"Ebs\": {\n" +
                    "                \"Status\": \"attached\",\n" +
                    "                \"DeleteOnTermination\": true,\n" +
                    "                \"VolumeId\": \"vol-615a1339\",\n" +
                    "                \"AttachTime\": \"2013-05-17T22:42:34.000Z\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"DeviceName\": \"/dev/sdf\",\n" +
                    "            \"Ebs\": {\n" +
                    "                \"Status\": \"attached\",\n" +
                    "                \"DeleteOnTermination\": false,\n" +
                    "                \"VolumeId\": \"vol-9f54b8dc\",\n" +
                    "                \"AttachTime\": \"2013-09-10T23:07:00.000Z\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ],\n" +
                    "}";

    @Test
    public void testEmptyPayload() throws Exception {
        jsonParameters.add(lambdaKey1);
        jsonParameters.add(lambdaKey2);
        jsonParameters.add(keyPayload);
        jsonParameters.add(keyPayloadNull);
        JsonPathParser jsonPathParser = new JsonPathParser(jsonParameters, new JenkinsLogger(System.out));
        Map<String, String> parseResult = jsonPathParser.parse(emptyPayload);

        assertEquals(0, parseResult.size());
    }

    final String emptyPayload = "";

    @Test
    public void testNullPayload() throws Exception {
        jsonParameters.add(lambdaKey1);
        jsonParameters.add(lambdaKey2);
        jsonParameters.add(keyPayload);
        jsonParameters.add(keyPayloadNull);
        JsonPathParser jsonPathParser = new JsonPathParser(jsonParameters, new JenkinsLogger(System.out));
        Map<String, String> parseResult = jsonPathParser.parse(nullPayload);

        assertEquals(0, parseResult.size());
    }

    final String nullPayload = null;
}