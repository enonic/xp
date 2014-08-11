package com.enonic.wem.core.event;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.event.EventPublisher;

public final class EventModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( EventPublisher.class ).to( EventPublisherImpl.class ).in( Singleton.class );
    }
}
