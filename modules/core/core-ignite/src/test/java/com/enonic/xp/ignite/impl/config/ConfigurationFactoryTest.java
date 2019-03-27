package com.enonic.xp.ignite.impl.config;

import java.nio.file.Paths;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.Assert.*;

public class ConfigurationFactoryTest
{
    private IgniteSettings igniteSettings;

    private ClusterConfig clusterConfig;

    private BundleContext bundleContext;

    @Before
    public void setUp()
        throws Exception
    {
        System.setProperty( "xp.home", Paths.get( "my", "xp", "home" ).toString() );
        this.bundleContext = Mockito.mock( BundleContext.class );

        this.igniteSettings = Mockito.mock( IgniteSettings.class );
        this.clusterConfig = Mockito.mock( ClusterConfig.class );
        Mockito.when( this.igniteSettings.metrics_log_frequency() ).thenReturn( 0 );
        Mockito.when( this.igniteSettings.connector_enabled() ).thenReturn( false );
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 47500 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 0 );
        Mockito.when( this.igniteSettings.discovery_tcp_reconnect() ).thenReturn( 2 );
        Mockito.when( this.igniteSettings.discovery_tcp_network_timeout() ).thenReturn( 5000L );
        Mockito.when( this.igniteSettings.discovery_tcp_socket_timeout() ).thenReturn( 2000L );
        Mockito.when( this.igniteSettings.discovery_tcp_ack_timeout() ).thenReturn( 2000L );
        Mockito.when( this.igniteSettings.discovery_tcp_join_timeout() ).thenReturn( 0L );
        Mockito.when( this.igniteSettings.discovery_tcp_stat_printFreq() ).thenReturn( 0 );
        Mockito.when( this.igniteSettings.off_heap_max_size() ).thenReturn( "512mb" );

        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );
        Mockito.when( this.clusterConfig.networkPublishHost() ).thenReturn( "localhost" );
    }

    @Test
    public void name()
        throws Exception
    {
        final IgniteConfiguration config = com.enonic.xp.ignite.impl.config.ConfigurationFactory.create().
            clusterConfig( createClusterConfig( "myNode" ) ).
            igniteConfig( this.igniteSettings ).
            bundleContext( this.bundleContext ).
            build().
            execute();

        assertEquals( "myNode", config.getConsistentId() );
    }

    @Test
    public void ignite_home()
        throws Exception
    {

        Mockito.when( this.igniteSettings.home() ).thenReturn( Paths.get( "fisk", "ost" ).toString() );

        final IgniteConfiguration config = com.enonic.xp.ignite.impl.config.ConfigurationFactory.create().
            clusterConfig( createClusterConfig( "myNode" ) ).
            igniteConfig( this.igniteSettings ).
            bundleContext( this.bundleContext ).
            build().
            execute();

        assertEquals( Paths.get( "fisk", "ost" ).toString(), config.getIgniteHome() );
    }

    @Test
    public void ignite_home_default()
        throws Exception
    {
        final IgniteConfiguration config = com.enonic.xp.ignite.impl.config.ConfigurationFactory.create().
            clusterConfig( createClusterConfig( "myNode" ) ).
            igniteConfig( this.igniteSettings ).
            bundleContext( this.bundleContext ).
            build().
            execute();

        assertEquals( Paths.get( "my", "xp", "home" ).toString(), config.getIgniteHome() );
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