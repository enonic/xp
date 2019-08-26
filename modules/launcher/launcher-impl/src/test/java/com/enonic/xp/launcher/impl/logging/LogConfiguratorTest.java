package com.enonic.xp.launcher.impl.logging;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;


import com.enonic.xp.launcher.impl.env.Environment;

public class LogConfiguratorTest
{
    @TempDir
    public Path temporaryFolder;

    private Environment env;

    @BeforeEach
    public void setup()
    {
        this.env = Mockito.mock( Environment.class );
        Mockito.when( this.env.getHomeDir() ).thenReturn( this.temporaryFolder.getRoot().toFile() );
    }

    private void writeLogbackFile( final String xml )
        throws Exception
    {
        final File configDir = Files.createDirectory(this.temporaryFolder.resolve( "config" ) ).toFile();
        final File logbackFile = new File( configDir, "logback.xml" );
        com.google.common.io.Files.write( xml, logbackFile, StandardCharsets.UTF_8 );
    }

    @Test
    public void testConfigurator()
        throws Exception
    {
        writeLogbackFile( "<configuration/>" );

        final LogConfigurator configurator = new LogConfigurator( this.env );
        configurator.configure();
    }

    @Test
    public void testConfigurator_error()
        throws Exception
    {
        writeLogbackFile( "<c>" );

        final LogConfigurator configurator = new LogConfigurator( this.env );
        configurator.configure();
    }

    @Test
    public void testConfigurator_wrongFile()
    {
        final LogConfigurator configurator = new LogConfigurator( this.env );
        configurator.configure();
    }
}
