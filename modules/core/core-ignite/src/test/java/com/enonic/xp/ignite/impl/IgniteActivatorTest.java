package com.enonic.xp.ignite.impl;

import java.nio.file.Paths;

import org.apache.ignite.Ignite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;
import com.enonic.xp.ignite.impl.config.IgniteSettings;
import com.enonic.xp.ignite.impl.config.TestDiscovery;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgniteActivatorTest
{
    @Mock
    private Ignite ignite;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private IgniteSettings igniteSettings;

    @Mock
    private ServiceRegistration<Ignite> serviceRegistration;

    @BeforeEach
    void setUp()
    {
        System.setProperty( "xp.home", Paths.get( "my", "xp", "home" ).toString() );
        lenient().when( this.igniteSettings.metrics_log_frequency() ).thenReturn( 0 );
        lenient().when( this.igniteSettings.connector_enabled() ).thenReturn( false );
        lenient().when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 47500 );
        lenient().when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 0 );
        lenient().when( this.igniteSettings.discovery_tcp_reconnect() ).thenReturn( 2 );
        lenient().when( this.igniteSettings.discovery_tcp_network_timeout() ).thenReturn( 5000L );
        lenient().when( this.igniteSettings.discovery_tcp_socket_timeout() ).thenReturn( 2000L );
        lenient().when( this.igniteSettings.discovery_tcp_ack_timeout() ).thenReturn( 2000L );
        lenient().when( this.igniteSettings.discovery_tcp_join_timeout() ).thenReturn( 0L );
        lenient().when( this.igniteSettings.discovery_tcp_stat_printFreq() ).thenReturn( 0 );
        lenient().when( this.igniteSettings.off_heap_max_size() ).thenReturn( "512mb" );
    }

    @Test
    void activateWithClusterDisabled()
    {
        final ClusterConfig clusterConfig = mock( ClusterConfig.class );
        when( clusterConfig.isEnabled() ).thenReturn( false );
        final IgniteActivator igniteActivator = new IgniteActivator( clusterConfig, ( configuration ) -> ignite );
        igniteActivator.activate( bundleContext, igniteSettings );
        verifyZeroInteractions( bundleContext );
    }

    @Test
    void activateWithClusterEnabled()
    {
        final IgniteActivator igniteActivator = new IgniteActivator( createClusterConfig( "ignite" ), ( configuration ) -> ignite );

        when( bundleContext.registerService( eq( Ignite.class ), same( ignite ), notNull() ) ).thenReturn( serviceRegistration );
        igniteActivator.activate( bundleContext, igniteSettings );
        verify( bundleContext, times( 1 ) ).registerService( eq( Ignite.class ), same( ignite ), notNull() );

        igniteActivator.deactivate();
        verify( serviceRegistration, times( 1 ) ).unregister();
    }

    private ClusterConfig createClusterConfig( final String name )
    {
        return new ClusterConfig()
        {
            @Override
            public NodeDiscovery discovery()
            {
                return TestDiscovery.from( "localhost", "192.168.0.1" );
            }

            @Override
            public ClusterNodeId name()
            {
                return ClusterNodeId.from( name );
            }

            @Override
            public boolean isEnabled()
            {
                return true;
            }

            @Override
            public String networkPublishHost()
            {
                return "127.0.0.1";
            }

            @Override
            public String networkHost()
            {
                return "127.0.0.1";
            }

            @Override
            public boolean isSessionReplicationEnabled()
            {
                return true;
            }
        };
    }
}