package com.enonic.xp.server.udc.impl;

import org.junit.jupiter.api.Test;
import org.osgi.service.component.ComponentContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UdcActivatorTest
{
    @Test
    void lifecycle_enabled()
    {
        final UdcConfig config = mock( UdcConfig.class );
        when( config.enabled() ).thenReturn( true );
        final ComponentContext componentContext = mock( ComponentContext.class );
        final UdcActivator udcActivator = new UdcActivator( config, componentContext );
        verify( componentContext ).enableComponent( UdcService.class.getName() );

        udcActivator.deactivate( componentContext );

        verify( componentContext ).disableComponent( UdcService.class.getName() );
    }

    @Test
    void lifecycle_disabled()
    {
        final UdcConfig config = mock( UdcConfig.class );
        when( config.enabled() ).thenReturn( false );
        final ComponentContext componentContext = mock( ComponentContext.class );
        final UdcActivator udcActivator = new UdcActivator( config, componentContext );
        udcActivator.deactivate( componentContext );
        verify( componentContext, never() ).enableComponent( UdcService.class.getName() );
    }
}