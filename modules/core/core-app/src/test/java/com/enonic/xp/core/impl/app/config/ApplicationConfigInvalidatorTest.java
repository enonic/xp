package com.enonic.xp.core.impl.app.config;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

import com.enonic.xp.core.impl.app.ApplicationConfigService;
import com.enonic.xp.core.impl.app.BundleBasedTest;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ApplicationConfigInvalidatorTest
    extends BundleBasedTest

{
    @Test
    public void lifecycle()
        throws Exception
    {
        final ApplicationConfigInvalidator invalidator = spy( ApplicationConfigInvalidator.class );
        final ApplicationConfigService mock = mock( ApplicationConfigService.class );
        invalidator.setApplicationConfigService( mock );
        invalidator.activate( getBundleContext() );

        final Bundle bundle = deploy( "app1", newBundle( "app1", true ) );

        final ResultCaptor<ServiceRegistration<ManagedService>> resultCaptor = new ResultCaptor<>();

        doAnswer( resultCaptor ).when( invalidator ).addingBundle( same( bundle ), any() );

        bundle.start();

        verify( invalidator ).addingBundle( same( bundle ), any() );

        final ServiceRegistration<ManagedService> reference = resultCaptor.result;

        bundle.stop();

        verify( invalidator ).removedBundle( same( bundle ), any(), same( reference ) );

        invalidator.deactivate();
    }

    private static class ResultCaptor<T>
        implements Answer<T>
    {
        T result;

        @Override
        public T answer( InvocationOnMock invocation )
            throws Throwable
        {
            result = (T) invocation.callRealMethod();
            return result;
        }
    }
}
