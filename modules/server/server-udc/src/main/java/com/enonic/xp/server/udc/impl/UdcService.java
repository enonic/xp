package com.enonic.xp.server.udc.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.internal.concurrent.RecurringJob;

@Component(enabled = false, configurationPid = "com.enonic.xp.server.udc")
public final class UdcService
{
    private final UdcScheduler scheduler;

    private final PingSender pingSender;

    private RecurringJob recurringJob;

    @Activate
    public UdcService( @Reference UdcScheduler scheduler, final UdcConfig config )
    {
        this.scheduler = scheduler;
        this.pingSender = new PingSender( new UdcInfoGenerator(), config.url() );
    }

    @Activate
    public void activate()
    {
        recurringJob = scheduler.scheduleWithFixedDelay( pingSender );
    }

    @Deactivate
    public void deactivate()
    {
        recurringJob.cancel();
    }
}
