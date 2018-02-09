package com.enonic.xp.ignite.impl.config;

import java.nio.file.Paths;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.Assert.*;

public class ConfigurationFactoryTest
{
    private com.enonic.xp.ignite.impl.config.IgniteSettings igniteSettings;

    @Before
    public void setUp()
        throws Exception
    {
        System.setProperty( "xp.home", Paths.get( "my", "xp", "home" ).toString() );
        this.igniteSettings = Mockito.mock( IgniteSettings.class );
    }

    @Test
    public void name()
        throws Exception
    {
        final IgniteConfiguration config = com.enonic.xp.ignite.impl.config.ConfigurationFactory.create().
            clusterConfig( createClusterConfig( "myNode" ) ).
            igniteConfig( this.igniteSettings ).
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
        };
    }


}