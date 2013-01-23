package com.enonic.wem.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.enonic.wem.core.home.HomeDir;

public class ConfigLoaderTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;

    private ConfigLoader configLoader;

    private ClassLoader classLoader;

    @Before
    public void setUp()
        throws Exception
    {
        this.classLoader = Mockito.mock( ClassLoader.class );

        this.homeDir = this.folder.newFolder( "cms-home" );
        this.configLoader = new ConfigLoader( new HomeDir( this.homeDir ) );
        this.configLoader.setClassLoader( this.classLoader );
    }

    private void setupSystemProperties()
    {
        final Properties props = new Properties();
        props.setProperty( "system.param", "system.value" );
        this.configLoader.addSystemProperties( props );
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "home.value" );
        props.setProperty( "override", "home" );
        props.setProperty( "interpolate", "${home.param} ${system.param}" );

        final File file = new File( this.homeDir, "config/cms.properties" );
        file.getParentFile().mkdirs();

        final FileOutputStream out = new FileOutputStream( file );
        props.store( out, "" );
        out.close();
    }

    private void setupClassPathProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "classpath.param", "classpath.value" );
        props.setProperty( "override", "classpath" );
        props.setProperty( "interpolate", "${classpath.param} ${system.param}" );

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store( out, "" );
        out.close();

        final ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
        Mockito.when( this.classLoader.getResourceAsStream( "com/enonic/wem/core/config/default.properties" ) ).thenReturn( in );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoDefaultProperties()
        throws Exception
    {
        this.configLoader.load();
    }

    @Test
    public void testDefaultConfig()
        throws Exception
    {
        setupClassPathProperties();

        final Properties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertEquals( 5, props.size() );
        Assert.assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        Assert.assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        Assert.assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        Assert.assertEquals( "classpath", props.getProperty( "override" ) );
        Assert.assertEquals( "classpath.value ${system.param}", props.getProperty( "interpolate" ) );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupClassPathProperties();
        setupHomeProperties();

        final Properties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertEquals( 6, props.size() );
        Assert.assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        Assert.assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        Assert.assertEquals( "home.value", props.getProperty( "home.param" ) );
        Assert.assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        Assert.assertEquals( "home", props.getProperty( "override" ) );
        Assert.assertEquals( "home.value ${system.param}", props.getProperty( "interpolate" ) );
    }

    @Test
    public void testSystemProperties()
        throws Exception
    {
        setupSystemProperties();
        setupClassPathProperties();

        final Properties props = this.configLoader.load();
        Assert.assertNotNull( props );
        Assert.assertEquals( 5, props.size() );
        Assert.assertEquals( this.homeDir.toString(), props.getProperty( "cms.home" ) );
        Assert.assertEquals( this.homeDir.toURI().toString(), props.getProperty( "cms.home.uri" ) );
        Assert.assertEquals( "classpath.value", props.getProperty( "classpath.param" ) );
        Assert.assertEquals( "classpath", props.getProperty( "override" ) );
        Assert.assertEquals( "classpath.value system.value", props.getProperty( "interpolate" ) );
    }
}
