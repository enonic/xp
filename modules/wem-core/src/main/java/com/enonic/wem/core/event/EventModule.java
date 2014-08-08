package com.enonic.wem.core.event;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.event.EventService;

public final class EventModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( EventService.class ).to( EventServiceImpl.class ).in( Singleton.class );
    }
}
