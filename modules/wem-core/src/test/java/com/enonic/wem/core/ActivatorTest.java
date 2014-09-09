package com.enonic.wem.core;

import java.io.File;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.inject.Guice;

@Ignore
public class ActivatorTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
    {
        final File homeDir = this.temporaryFolder.newFolder( "karaf.home" );
        System.setProperty( "karaf.home", homeDir.getAbsolutePath() );
    }

    @Test
    public void testCreateInjector()
    {
        Guice.createInjector( new Activator() );
    }
}
