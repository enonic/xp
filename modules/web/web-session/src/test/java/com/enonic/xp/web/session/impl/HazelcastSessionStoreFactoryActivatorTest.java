package com.enonic.xp.web.session.impl;

import org.eclipse.jetty.session.SessionCacheFactory;
import org.eclipse.jetty.session.SessionDataStoreFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.hazelcast.core.HazelcastInstance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastSessionStoreFactoryActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private WebSessionStoreConfigService webSessionstoreConfigService;

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

        final HazelcastSessionStoreFactoryActivator hzSessionDataStoreFactoryActivator =
            new HazelcastSessionStoreFactoryActivator( bundleContext, mock( HazelcastInstance.class ), webSessionstoreConfigService );

        hzSessionDataStoreFactoryActivator.activate();

        verify( webSessionstoreConfigService ).getSavePeriodSeconds();
        verify( webSessionstoreConfigService ).getGracePeriodSeconds();
        verify( bundleContext ).registerService( same( SessionDataStoreFactory.class ), any( SessionDataStoreFactory.class ), isNull() );
        verify( bundleContext ).registerService( same( SessionCacheFactory.class ), any( SessionCacheFactory.class ), isNull() );

        hzSessionDataStoreFactoryActivator.deactivate();
        verify( sessionDataStoreFactoryServiceRegistration, times( 1 ) ).unregister();
        verify( sessionCacheFactoryServiceRegistration, times( 1 ) ).unregister();
    }
}
