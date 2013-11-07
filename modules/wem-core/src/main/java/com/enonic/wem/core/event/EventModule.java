package com.enonic.wem.core.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

public final class EventModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final EventBus eventBus = new EventBus();
        bind( EventBus.class ).toInstance( eventBus );
        bindListener( new SubscribeMatcher(), new SubscribeTypeListener( eventBus ) );
    }
}
