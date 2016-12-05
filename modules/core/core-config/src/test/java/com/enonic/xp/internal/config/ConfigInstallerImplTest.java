package com.enonic.xp.internal.config;

import java.io.File;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ConfigInstallerImplTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ConfigInstallerImpl installer;

    private ConfigurationAdmin configurationAdmin;

    @Before
    public void setup()
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        this.configurationAdmin = Mockito.mock( ConfigurationAdmin.class );

        this.installer = new ConfigInstallerImpl();
        this.installer.setConfigurationAdmin( this.configurationAdmin );
        this.installer.activate( context );
    }

    private Configuration mockGetConfiguration()
        throws Exception
    {
        final Configuration config = Mockito.mock( Configuration.class );
        Mockito.when( this.configurationAdmin.getConfiguration( "com.foo.bar", null ) ).thenReturn( config );
        return config;
    }

    @Test
    public void updateConfig_empty()
        throws Exception
    {
        final Configuration config = mockGetConfiguration();
        final File file = this.temporaryFolder.newFile( "com.foo.bar.cfg" );

        this.installer.updateConfig( file );
        Mockito.verify( config, Mockito.times( 0 ) ).update( Mockito.any() );
    }

    @Test
    public void updateConfig_changed()
        throws Exception
    {
        final Hashtable<String, Object> oldMap = new Hashtable<>();
        oldMap.put( "a", 1 );

        final Configuration config = mockGetConfiguration();
        Mockito.when( config.getProperties() ).thenReturn( oldMap );
        final File file = this.temporaryFolder.newFile( "com.foo.bar.cfg" );

        this.installer.updateConfig( file );
        Mockito.verify( config, Mockito.times( 1 ) ).update( Mockito.any() );
    }

    @Test
    public void updateConfig_unchanged()
        throws Exception
    {
        final Hashtable<String, Object> oldMap = new Hashtable<>();

        final Configuration config = mockGetConfiguration();
        Mockito.when( config.getProperties() ).thenReturn( oldMap );
        final File file = this.temporaryFolder.newFile( "com.foo.bar.cfg" );

        this.installer.updateConfig( file );
        Mockito.verify( config, Mockito.times( 0 ) ).update( Mockito.any() );
    }

    @Test
    public void deleteConfig_new()
        throws Exception
    {
        final Configuration config = mockGetConfiguration();
        this.installer.deleteConfig( "com.foo.bar.cfg" );
        Mockito.verify( config, Mockito.times( 1 ) ).delete();
    }

    @Test
    public void deleteConfig_existing()
        throws Exception
    {
        final Configuration config = Mockito.mock( Configuration.class );
        Mockito.when( this.configurationAdmin.listConfigurations( "(config.filename=com.foo.bar.cfg)" ) ).thenReturn(
            new Configuration[]{config} );

        this.installer.deleteConfig( "com.foo.bar.cfg" );
        Mockito.verify( config, Mockito.times( 1 ) ).delete();
    }
}
