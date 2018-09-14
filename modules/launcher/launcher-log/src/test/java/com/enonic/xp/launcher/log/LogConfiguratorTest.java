package com.enonic.xp.launcher.impl.logging;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import com.enonic.xp.launcher.log.LogConfigurator;

public class LogConfiguratorTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
    {
        System.setProperty( "xp.home", this.temporaryFolder.getRoot().getAbsolutePath() );
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

        final LogConfigurator configurator = new LogConfigurator();
        configurator.activate();
    }

    @Test
    public void testConfigurator_error()
        throws Exception
    {
        writeLogbackFile( "<c>" );

        final LogConfigurator configurator = new LogConfigurator();
        configurator.activate();
    }

    @Test
    public void testConfigurator_wrongFile()
    {
        final LogConfigurator configurator = new LogConfigurator();
        configurator.activate();
    }
}
