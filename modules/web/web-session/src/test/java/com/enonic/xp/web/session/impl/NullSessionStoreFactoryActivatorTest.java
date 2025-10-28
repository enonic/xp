package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.session.AbstractSessionCache;
import org.eclipse.jetty.session.SessionCache;
import org.eclipse.jetty.session.SessionCacheFactory;
import org.eclipse.jetty.session.SessionDataStoreFactory;
import org.eclipse.jetty.session.SessionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NullSessionStoreFactoryActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<SessionDataStoreFactory> sessionDataStoreFactoryServiceRegistration;

    @Mock
    private ServiceRegistration<SessionCacheFactory> sessionCacheFactoryServiceRegistration;

    @Test
    void verifyActivateDeactivate()
    {
        when( bundleContext.registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() ) ).
            thenReturn( sessionDataStoreFactoryServiceRegistration );
        when( bundleContext.registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() ) ).
            thenReturn( sessionCacheFactoryServiceRegistration );

        final NullSessionStoreFactoryActivator nullSessionDataStoreFactoryActivator = new NullSessionStoreFactoryActivator( bundleContext );

        nullSessionDataStoreFactoryActivator.activate();

        final ArgumentCaptor<SessionCacheFactory> sessionFactoryCaptor = ArgumentCaptor.forClass( SessionCacheFactory.class );

        verify( bundleContext ).registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() );
        verify( bundleContext ).registerService( same( SessionCacheFactory.class ), sessionFactoryCaptor.capture(), isNull() );

        final SessionCache sessionCache = sessionFactoryCaptor.getValue().getSessionCache( mock( SessionHandler.class ) );
        assertEquals( AbstractSessionCache.NEVER_EVICT, sessionCache.getEvictionPolicy() );

        nullSessionDataStoreFactoryActivator.deactivate();
        verify( sessionDataStoreFactoryServiceRegistration, times( 1 ) ).unregister();
        verify( sessionCacheFactoryServiceRegistration, times( 1 ) ).unregister();
    }
}
