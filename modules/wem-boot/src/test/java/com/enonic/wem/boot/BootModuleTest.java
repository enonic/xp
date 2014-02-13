package com.enonic.wem.boot;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.inject.Guice;

import com.enonic.wem.core.home.HomeDir;

public class BootModuleTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testModule()
    {
        new HomeDir( this.folder.getRoot() );
        Guice.createInjector( new BootModule() );
    }
}
