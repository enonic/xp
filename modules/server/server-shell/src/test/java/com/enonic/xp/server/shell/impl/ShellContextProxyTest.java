package com.enonic.xp.server.shell.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.*;

public class ShellContextProxyTest
{
    private BundleContext context;

    private ShellContextProxy builder;

    @Before
    public void setup()
    {
        this.context = Mockito.mock( BundleContext.class );
        this.builder = new ShellContextProxy( this.context );
    }

    @Test
    public void testDelegate()
    {
        final BundleContext proxy = this.builder.build();
        proxy.getBundle();

        Mockito.verify( this.context, Mockito.times( 1 ) ).getBundle();
    }

    @Test
    public void testGetProperty_override()
    {
        this.builder.property( "key1", "value1" );

        final BundleContext proxy = this.builder.build();
        assertEquals( "value1", proxy.getProperty( "key1" ) );

        Mockito.verify( this.context, Mockito.times( 0 ) ).getProperty( "key1" );
    }

    @Test
    public void testGetProperty_noOverride()
    {
        final BundleContext proxy = this.builder.build();
        proxy.getProperty( "key1" );

        Mockito.verify( this.context, Mockito.times( 1 ) ).getProperty( "key1" );
    }
}
