package com.xti.jenkins.plugin.awslambda.invoke;

import com.xti.jenkins.plugin.awslambda.exception.LambdaInvokeException;
import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaInvokeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LambdaInvokerTest {

    @Mock
    private LambdaInvokeService service;

    @Mock
    private JenkinsLogger logger;

    @Test
    public void testInvokeSuccess() throws Exception {
        when(service.invokeLambdaFunction(any(InvokeConfig.class))).thenReturn("{\"key2\":\"value2\"}");

        LambdaInvoker invoker = new LambdaInvoker(service, logger);

        InvokeConfig invokeConfig = new InvokeConfig("function", "{\"key\":\"value\"}", true, Collections.singletonList(new JsonParameter("LAMBDA_ENV_VAR", "$.key2")));

        LambdaInvocationResult result = invoker.invoke(invokeConfig);

        assertTrue(result.isSuccess());
        assertEquals("value2", result.getInjectables().get("LAMBDA_ENV_VAR"));
    }

    @Test
    public void testInvokeFailure() throws Exception {
        when(service.invokeLambdaFunction(any(InvokeConfig.class))).thenThrow(new LambdaInvokeException("Function returned error of type: Handled"));

        LambdaInvoker invoker = new LambdaInvoker(service, logger);

        InvokeConfig invokeConfig = new InvokeConfig("function", "{\"key\":\"value\"}", true, new ArrayList<JsonParameter>());

        LambdaInvocationResult result = invoker.invoke(invokeConfig);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getInjectables().size());

        verify(logger, times(1)).log("%nStarting lambda invocation.");
    }
}