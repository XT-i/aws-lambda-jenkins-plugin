package com.xti.jenkins.plugin.awslambda.publish;

import com.xti.jenkins.plugin.awslambda.service.JenkinsLogger;
import com.xti.jenkins.plugin.awslambda.service.LambdaPublishService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Magnus Sulland on 4/08/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class LambdaPublishTest {

    @Mock
    private LambdaPublishService service;

    @Mock
    private JenkinsLogger logger;

    @Test
    public void testPublishSuccess() throws Exception {
        when(service.publishLambda(any(PublishConfig.class))).thenReturn(new LambdaPublishServiceResponse("VERSION", "ALIAS", true));

        LambdaPublisher publisher = new LambdaPublisher(service, logger);

        PublishConfig publishConfig = new PublishConfig("ALIAS", "ARN", "DESCRIPTION");

        LambdaPublishServiceResponse result = publisher.publish(publishConfig);

        verify(logger, times(1)).log("%nStarting lambda publish procedure");

        assertEquals(publishConfig.getFunctionAlias(), result.getFunctionAlias());
        assertEquals(result.getFunctionVersion(), "VERSION");

        assertTrue(result.getSuccess());
    }

    @Test
    public void testPublishFailure() throws Exception {
        when(service.publishLambda(any(PublishConfig.class))).thenReturn(new LambdaPublishServiceResponse("VERSION", "ALIAS", false));

        LambdaPublisher publisher = new LambdaPublisher(service, logger);

        PublishConfig publishConfig = new PublishConfig("ALIAS", "ARN", "DESCRIPTION");

        LambdaPublishServiceResponse result = publisher.publish(publishConfig);

        verify(logger, times(1)).log("%nStarting lambda publish procedure");

        assertEquals(publishConfig.getFunctionAlias(), result.getFunctionAlias());
        assertEquals(result.getFunctionVersion(), "VERSION");

        assertFalse(result.getSuccess());
    }
}
