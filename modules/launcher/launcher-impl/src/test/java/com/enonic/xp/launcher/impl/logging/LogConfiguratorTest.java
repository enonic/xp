package com.enonic.xp.launcher.impl.logging;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.launcher.impl.env.Environment;

public class LogConfiguratorTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Environment env;

    @Before
    public void setup()
    {
        this.env = Mockito.mock( Environment.class );
        Mockito.when( this.env.getHomeDir() ).thenReturn( this.temporaryFolder.getRoot() );
    }

    private void writeLogbackFile( final String xml )
        throws Exception
    {
        final File configDir = this.temporaryFolder.newFolder( "config" );
        final File logbackFile = new File( configDir, "logback.xml" );
        Files.write( xml, logbackFile, Charsets.UTF_8 );
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
