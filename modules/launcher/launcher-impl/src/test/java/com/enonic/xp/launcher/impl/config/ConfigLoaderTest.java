package com.enonic.xp.launcher.impl.config;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.enonic.xp.launcher.impl.env.Environment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigLoaderTest
{
    @TempDir
    public Path temporaryFolder;

    private Path homeDir;

    private ConfigLoader configLoader;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) );

        final Environment env = Mockito.mock( Environment.class );
        Mockito.when( env.getHomeDir() ).thenReturn( this.homeDir.toFile() );

        this.configLoader = new ConfigLoader( env );
    }

    private void setupHomeProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.setProperty( "home.param", "home.value" );
        props.setProperty( "home.other.param ", " home.other.value " );

        final Path file = this.homeDir.resolve( "config/system.properties" );
        Files.createDirectories( file.getParent() );

        try (final Writer out = Files.newBufferedWriter( file ))
        {
            props.store( out, "" );
        }
    }

    @Test
    public void testDefaultConfig()
        throws Exception
    {
        final ConfigProperties props = this.configLoader.load();
        assertNotNull( props );
        assertTrue( !props.isEmpty() );
    }

    @Test
    public void testHomeConfig()
        throws Exception
    {
        setupHomeProperties();

        final ConfigProperties props = this.configLoader.load();
        assertNotNull( props );
        assertTrue( props.size() > 2 );
        assertEquals( "home.value", props.get( "home.param" ) );
        assertEquals( "home.other.value", props.get( "home.other.param" ) );
    }
}
