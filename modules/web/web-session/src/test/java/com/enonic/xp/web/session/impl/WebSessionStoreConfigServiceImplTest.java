package com.enonic.xp.web.session.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentContext;

import com.enonic.xp.cluster.ClusterConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSessionStoreConfigServiceImplTest
{
    @Mock
    private ClusterConfig clusterConfig;

    @Mock
    private WebSessionStoreConfig webSessionstoreConfig;

    @Mock
    private ComponentContext componentContext;

    @Test
    void configuration()
    {

        when( webSessionstoreConfig.savePeriodSeconds() ).thenReturn( 10 );
        when( webSessionstoreConfig.gracePeriodSeconds() ).thenReturn( 20 );

        final WebSessionStoreConfigServiceImpl webSessionStoreConfigServiceImpl =
            new WebSessionStoreConfigServiceImpl( webSessionstoreConfig, clusterConfig );
        assertEquals( 10, webSessionStoreConfigServiceImpl.getSavePeriodSeconds() );
        assertEquals( 20, webSessionStoreConfigServiceImpl.getGracePeriodSeconds() );
    }

    @Test
    void activate_replicated()
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( webSessionstoreConfig.storeMode() ).thenReturn( "replicated" );

        final WebSessionStoreConfigServiceImpl webSessionStoreConfigServiceImpl =
            new WebSessionStoreConfigServiceImpl( webSessionstoreConfig, clusterConfig );
        webSessionStoreConfigServiceImpl.activate( componentContext );

        verify( componentContext ).enableComponent( HazelcastSessionStoreFactoryActivator.class.getName() );

        webSessionStoreConfigServiceImpl.deactivate( componentContext );

        verify( componentContext ).disableComponent( HazelcastSessionStoreFactoryActivator.class.getName() );
    }

    @Test
    void activate_nonPersistent()
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( webSessionstoreConfig.storeMode() ).thenReturn( "non-persistent" );

        final WebSessionStoreConfigServiceImpl webSessionStoreConfigServiceImpl =
            new WebSessionStoreConfigServiceImpl( webSessionstoreConfig, clusterConfig );
        webSessionStoreConfigServiceImpl.activate( componentContext );

        verify( componentContext ).enableComponent( NullSessionStoreFactoryActivator.class.getName() );

        webSessionStoreConfigServiceImpl.deactivate( componentContext );

        verify( componentContext ).disableComponent( NullSessionStoreFactoryActivator.class.getName() );
    }

    @Test
    void activate_notClustered()
    {
        when( clusterConfig.isEnabled() ).thenReturn( false );
        when( webSessionstoreConfig.storeMode() ).thenReturn( "replicated" );

        final WebSessionStoreConfigServiceImpl webSessionStoreConfigServiceImpl =
            new WebSessionStoreConfigServiceImpl( webSessionstoreConfig, clusterConfig );
        webSessionStoreConfigServiceImpl.activate( componentContext );

        verify( componentContext ).enableComponent( NullSessionStoreFactoryActivator.class.getName() );

        webSessionStoreConfigServiceImpl.deactivate( componentContext );

        verify( componentContext ).disableComponent( NullSessionStoreFactoryActivator.class.getName() );
    }
}