package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class NodeSettingsBuilderTest
{
    private NodeSettingsBuilder builder;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
        throws Exception
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        this.builder = new NodeSettingsBuilder( context );

        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );
    }

    @Test
    public void settings_default()
    {
        final Map<String, String> map = Maps.newHashMap();
        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        assertEquals( 15, settings.getAsMap().size() );
        assertSettings( System.getProperty( "xp.home" ) + "/repo/index", settings );
    }

    @Test
    public void settings_override()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "path", "/to/some/other/path" );

        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        assertEquals( 15, settings.getAsMap().size() );
        assertSettings( "/to/some/other/path", settings );
    }

    private void assertSettings( String pathValue, final Settings settings )
    {
        assertEquals( "local-node", settings.get( "name" ) );
        assertEquals( "false", settings.get( "client" ) );
        assertEquals( "true", settings.get( "data" ) );
        assertEquals( "false", settings.get( "http.enabled" ) );
        assertEquals( "mycluster", settings.get( "cluster.name" ) );
        assertEquals( "127.0.0.1", settings.get( "network.host" ) );
        assertEquals( "false", settings.get( "discovery.zen.ping.multicast.enabled" ) );
        assertEquals( "false", settings.get( "cluster.routing.allocation.disk.threshold_enabled" ) );
        assertEquals( pathValue, settings.get( "path" ) );
        assertEquals( pathValue + "/data", settings.get( "path.data" ) );
        assertEquals( pathValue + "/work", settings.get( "path.work" ) );
        assertEquals( pathValue + "/conf", settings.get( "path.conf" ) );
        assertEquals( pathValue + "/logs", settings.get( "path.logs" ) );
        assertEquals( pathValue + "/plugins", settings.get( "path.plugins" ) );
    }
}
