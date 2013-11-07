package com.enonic.wem.core.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

final class SubscribeTypeListener
    implements TypeListener
{
    private final EventBus eventBus;

    public SubscribeTypeListener( final EventBus eventBus )
    {
        this.eventBus = eventBus;
    }

    @Override
    public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter )
    {
        encounter.register( new InjectionListener<I>()
        {
            public void afterInjection( I injectee )
            {
                eventBus.register( injectee );
            }
        } );
    }
}
