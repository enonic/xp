package com.enonic.xp.ignite.impl.config;

import java.nio.file.Paths;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.Assert.*;

public class ConfigurationFactoryTest
{
    private com.enonic.xp.ignite.impl.config.IgniteSettings igniteSettings;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp()
        throws Exception
    {
        System.setProperty( "xp.home", temporaryFolder.getRoot().toPath().toString() );
        this.igniteSettings = Mockito.mock( IgniteSettings.class );

        Mockito.when( this.igniteSettings.home() ).
            thenReturn( "work/ignite" );
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
        final IgniteConfiguration config = com.enonic.xp.ignite.impl.config.ConfigurationFactory.create().
            clusterConfig( createClusterConfig( "myNode" ) ).
            igniteConfig( this.igniteSettings ).
            build().
            execute();

        assertEquals( Paths.get( temporaryFolder.getRoot().getPath(), "work", "ignite" ).toString(), config.getIgniteHome() );
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