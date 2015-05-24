package com.xti.jenkins.plugin.awslambda.service;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.LogType;
import com.xti.jenkins.plugin.awslambda.invoke.InvokeConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LambdaInvokeServiceTest {

    @Mock
    private AWSLambdaClient awsLambdaClient;

    @Mock
    private JenkinsLogger jenkinsLogger;

    @Captor
    private ArgumentCaptor<InvokeRequest> invokeRequestArg;

    private LambdaInvokeService lambdaInvokeService;

    @Before
    public void setUp() throws Exception {
        lambdaInvokeService = new LambdaInvokeService(awsLambdaClient, jenkinsLogger);
    }

    @Test
    public void testProcessLambdaDeployment() throws Exception {

    }

    @Test
    public void testInvokeLambdaFunctionAsynchronous() throws Exception {
        InvokeConfig invokeConfig = new InvokeConfig("function", "{\"key1\": \"value1\"}", false, null);

        when(awsLambdaClient.invoke(any(InvokeRequest.class)))
                .thenReturn(new InvokeResult());

        String result = lambdaInvokeService.invokeLambdaFunction(invokeConfig);

        verify(awsLambdaClient, times(1)).invoke(invokeRequestArg.capture());
        InvokeRequest invokeRequest = invokeRequestArg.getValue();
        assertEquals("function", invokeRequest.getFunctionName());
        assertEquals("{\"key1\": \"value1\"}", new String(invokeRequest.getPayload().array(), Charset.forName("UTF-8")));
        assertEquals(InvocationType.Event.toString(), invokeRequest.getInvocationType());
        verify(jenkinsLogger).log(eq("Lambda invoke request:%n%s%nPayload:%n%s%n"), anyVararg());
        verify(jenkinsLogger).log(eq("Lambda invoke response:%n%s%nPayload:%n%s%n"), anyVararg());
        assertEquals("", result);
    }

    @Test
    public void testInvokeLambdaFunctionSynchronous() throws Exception {
        final String logBase64 = "bGFtYmRh";
        final String log = "lambda";
        final String requestPayload = "{\"key1\": \"value1\"}";
        final String responsePayload = "{\"key2\": \"value2\"}";

        InvokeConfig invokeConfig = new InvokeConfig("function", requestPayload, true, null);

        InvokeResult invokeResult = new InvokeResult()
                .withLogResult(logBase64)
                .withPayload(ByteBuffer.wrap(responsePayload.getBytes()));

        when(awsLambdaClient.invoke(any(InvokeRequest.class)))
                .thenReturn(invokeResult);

        String result = lambdaInvokeService.invokeLambdaFunction(invokeConfig);

        verify(awsLambdaClient, times(1)).invoke(invokeRequestArg.capture());
        InvokeRequest invokeRequest = invokeRequestArg.getValue();
        assertEquals("function", invokeRequest.getFunctionName());
        assertEquals(LogType.Tail.toString(), invokeRequest.getLogType());
        assertEquals(requestPayload, new String(invokeRequest.getPayload().array(), Charset.forName("UTF-8")));
        assertEquals(InvocationType.RequestResponse.toString(), invokeRequest.getInvocationType());

        ArgumentCaptor<String> stringArgs = ArgumentCaptor.forClass(String.class);
        verify(jenkinsLogger).log(eq("Lambda invoke request:%n%s%nPayload:%n%s%n"), stringArgs.capture());
        List<String> stringArgValues = stringArgs.getAllValues();
        assertEquals(Arrays.asList(invokeRequest.toString(), requestPayload), stringArgValues);

        verify(jenkinsLogger).log(eq("Log:%n%s%n"), eq(log));

        stringArgs = ArgumentCaptor.forClass(String.class);
        verify(jenkinsLogger).log(eq("Lambda invoke response:%n%s%nPayload:%n%s%n"), stringArgs.capture());
        stringArgValues = stringArgs.getAllValues();
        assertEquals(Arrays.asList(invokeResult.toString(), responsePayload), stringArgValues);

        assertEquals(responsePayload, result);
    }
}