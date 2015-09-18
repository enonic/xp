package com.enonic.xp.core.impl.server;

import java.io.File;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

import static org.junit.Assert.*;

public class ServerInfoImplTest
{
    @Test
    public void testInfo()
    {
        final File homeDir = new File( "./home" );
        final File installDir = new File( "./install" );

        final BundleContext context = Mockito.mock( BundleContext.class );
        Mockito.when( context.getProperty( "xp.name" ) ).thenReturn( "demo" );
        Mockito.when( context.getProperty( "xp.home" ) ).thenReturn( homeDir.toString() );
        Mockito.when( context.getProperty( "xp.install" ) ).thenReturn( installDir.toString() );

        final ComponentContext componentContext = Mockito.mock( ComponentContext.class );
        Mockito.when( componentContext.getBundleContext() ).thenReturn( context );

        final ServerInfoImpl info = new ServerInfoImpl();
        info.initialize( componentContext );

        assertNotNull( info.getBuildInfo() );
        assertEquals( "demo", info.getName() );
        assertEquals( homeDir, info.getHomeDir() );
        assertEquals( installDir, info.getInstallDir() );
    }
}
