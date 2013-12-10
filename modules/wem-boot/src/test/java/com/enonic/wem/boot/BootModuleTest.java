package com.enonic.wem.boot;

import javax.servlet.ServletContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

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
        Guice.createInjector( new BootModule( Mockito.mock( ServletContext.class ) ) );
    }
}
