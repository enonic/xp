package com.enonic.xp.server.udc.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UdcServiceTest
{
    private UdcService service;

    private UdcConfig config;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.service = new UdcService();
        this.service.delay = 100L;
        this.service.interval = 10000L;

        this.config = Mockito.mock( UdcConfig.class );
        Mockito.when( this.config.enabled() ).thenReturn( false );
        Mockito.when( this.config.url() ).thenReturn( "http://localhost:8080" );
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
