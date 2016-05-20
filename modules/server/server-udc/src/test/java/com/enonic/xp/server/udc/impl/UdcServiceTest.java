package com.enonic.xp.server.udc.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UdcServiceTest
{
    private UdcService service;

    private UdcConfig config;

    @Before
    public void setup()
        throws Exception
    {
        this.service = new UdcService();

        this.config = Mockito.mock( UdcConfig.class );
        Mockito.when( this.config.enabled() ).thenReturn( false );
        Mockito.when( this.config.delay() ).thenReturn( 100L );
        Mockito.when( this.config.url() ).thenReturn( "http://localhost:8080" );
        Mockito.when( this.config.interval() ).thenReturn( 10000L );
    }

    @Test
    public void testDisabled()
        throws Exception
    {
        this.service.activate( this.config );
        this.service.deactivate();
    }

    @Test
    public void testEnabled()
        throws Exception
    {
        Mockito.when( this.config.enabled() ).thenReturn( true );

        this.service.activate( this.config );
        Thread.sleep( 200L );
        this.service.deactivate();
    }
}
