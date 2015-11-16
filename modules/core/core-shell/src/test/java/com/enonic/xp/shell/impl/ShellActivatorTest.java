package com.enonic.xp.shell.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.*;

public class ShellActivatorTest
{
    private ShellActivator activator;

    private ShellConfig config;

    private BundleContext context;

    private BundleActivator subActivator;

    @Before
    public void setup()
    {
        this.activator = new ShellActivator();
        this.config = Mockito.mock( ShellConfig.class );
        this.context = Mockito.mock( BundleContext.class );
        this.subActivator = Mockito.mock( BundleActivator.class );
    }

    @Test
    public void checkActivators()
    {
        assertEquals( 5, this.activator.activators.size() );
    }

    @Test
    public void lifecycle_enabled()
        throws Exception
    {
        this.activator.activators.clear();
        this.activator.activators.add( this.subActivator );

        Mockito.when( this.config.enabled() ).thenReturn( true );
        Mockito.when( this.config.telnet_ip() ).thenReturn( "127.0.0.1" );
        Mockito.when( this.config.telnet_port() ).thenReturn( 5555 );
        Mockito.when( this.config.telnet_maxConnect() ).thenReturn( 2 );
        Mockito.when( this.config.telnet_socketTimeout() ).thenReturn( 0 );

        this.activator.activate( this.context, this.config );
        Mockito.verify( this.subActivator, Mockito.times( 1 ) ).start( this.activator.context );

        this.activator.deactivate();
        Mockito.verify( this.subActivator, Mockito.times( 1 ) ).stop( this.activator.context );

        assertEquals( "--nointeractive", this.activator.context.getProperty( "gosh.args" ) );
        assertEquals( "127.0.0.1", this.activator.context.getProperty( "osgi.shell.telnet.ip" ) );
        assertEquals( "5555", this.activator.context.getProperty( "osgi.shell.telnet.port" ) );
        assertEquals( "2", this.activator.context.getProperty( "osgi.shell.telnet.maxconn" ) );
        assertEquals( "0", this.activator.context.getProperty( "osgi.shell.telnet.socketTimeout" ) );
    }

    @Test
    public void lifecycle_notEnabled()
        throws Exception
    {
        this.activator.activators.clear();
        this.activator.activators.add( this.subActivator );

        Mockito.when( this.config.enabled() ).thenReturn( false );

        this.activator.activate( this.context, this.config );
        Mockito.verify( this.subActivator, Mockito.times( 0 ) ).start( this.activator.context );

        this.activator.deactivate();
        Mockito.verify( this.subActivator, Mockito.times( 0 ) ).stop( this.activator.context );
    }
}
