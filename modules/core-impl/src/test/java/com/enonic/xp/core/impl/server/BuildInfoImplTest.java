package com.enonic.xp.core.impl.server;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.*;

public class BuildInfoImplTest
{
    @Test
    public void testInfo()
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        Mockito.when( context.getProperty( "xp.build.branch" ) ).thenReturn( "master" );
        Mockito.when( context.getProperty( "xp.build.hash" ) ).thenReturn( "123456" );
        Mockito.when( context.getProperty( "xp.build.shortHash" ) ).thenReturn( "123" );
        Mockito.when( context.getProperty( "xp.build.timestamp" ) ).thenReturn( "2015-11-11T22:11:00" );

        final BuildInfoImpl info = new BuildInfoImpl( context );
        assertEquals( "master", info.getBranch() );
        assertEquals( "123456", info.getHash() );
        assertEquals( "123", info.getShortHash() );
        assertEquals( "2015-11-11T22:11:00", info.getTimestamp() );
    }
}
