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

        // TODO: Check if settings is OK
    }

    @Test
    public void settings_override()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "path", "/to/some/other/path" );

        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        assertEquals( 15, settings.getAsMap().size() );

        // TODO: Check if settings is OK

        assertEquals( "/to/some/other/path", settings.get( "path" ) );
    }
}
