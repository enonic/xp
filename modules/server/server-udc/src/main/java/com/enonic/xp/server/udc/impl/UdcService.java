package com.enonic.xp.server.udc.impl;

import java.util.Timer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(immediate = true, configurationPid = "com.enonic.xp.server.udc")
public final class UdcService
{
    private Timer timer;

    @Activate
    public void activate( final UdcConfig config )
    {
        this.timer = new Timer( "udc" );
        if ( !config.enabled() )
        {
            return;
        }

        final UdcInfoGenerator generator = new UdcInfoGenerator();
        final PingSenderImpl sender = new PingSenderImpl( generator, config.url() );

        final PingTask task = new PingTask( sender );
        this.timer.schedule( task, config.delay(), config.interval() );
    }

    @Deactivate
    public void deactivate()
    {
        this.timer.cancel();
    }
}
