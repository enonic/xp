package com.enonic.xp.server.udc.impl;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true, configurationPid = "com.enonic.xp.server.udc")
public class UdcActivator
{
    @Activate
    public UdcActivator( final UdcConfig config, final ComponentContext context )
    {
        if ( config.enabled() )
        {
            context.enableComponent( UdcService.class.getName() );
        }
    }

    @Deactivate
    public void deactivate( final ComponentContext context )
    {
        context.disableComponent( UdcService.class.getName() );
    }
}