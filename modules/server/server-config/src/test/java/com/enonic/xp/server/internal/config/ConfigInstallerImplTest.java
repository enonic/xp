package com.enonic.xp.server.internal.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.enonic.xp.core.internal.Dictionaries;


public class ConfigInstallerImplTest
{
    @TempDir
    public Path temporaryFolder;

    private ConfigInstallerImpl installer;

    private ConfigurationAdmin configurationAdmin;

    @BeforeEach
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
        final File file = Files.createFile( this.temporaryFolder.resolve( "com.foo.bar.cfg" ) ).toFile();

        this.installer.updateConfig( file );
        Mockito.verify( config, Mockito.times( 0 ) ).update( Mockito.any() );
    }

    @Test
    public void updateConfig_changed()
        throws Exception
    {
        final Configuration config = mockGetConfiguration();
        Mockito.when( config.getProperties() ).thenReturn( Dictionaries.of( "a", 1 ) );
        final File file = Files.createFile( this.temporaryFolder.resolve( "com.foo.bar.cfg" ) ).toFile();

        this.installer.updateConfig( file );
        Mockito.verify( config, Mockito.times( 1 ) ).update( Mockito.any() );
    }

    @Test
    public void updateConfig_unchanged()
        throws Exception
    {
        final Configuration config = mockGetConfiguration();
        Mockito.when( config.getProperties() ).thenReturn( Dictionaries.of() );
        final File file = Files.createFile( this.temporaryFolder.resolve( "com.foo.bar.cfg" ) ).toFile();

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
