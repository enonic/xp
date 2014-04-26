package com.enonic.wem.launcher.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.launcher.home.HomeDir;

public class ConfigLoaderTest
{
    private final static int NUM_PROPS = 3;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;

    private ConfigLoader configLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.homeDir = this.folder.newFolder( "home" );
        this.configLoader = new ConfigLoader( new HomeDir( this.homeDir ) );
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "home.value" );
        props.setProperty( "home.other.param    ", "   home.other.value   " );

        final File file = new File( this.homeDir, "etc/system.properties" );
        file.getParentFile().mkdirs();

        final FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    @Test
    public void testDefaultConfig()
        throws Exception
    {
        final ConfigProperties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertEquals( NUM_PROPS, props.size() );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupHomeProperties();

        final ConfigProperties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertEquals( NUM_PROPS + 2, props.size() );
        Assert.assertEquals( "home.value", props.get( "home.param" ) );
        Assert.assertEquals( "home.other.value", props.get( "home.other.param" ) );
    }
}
