package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.event.Event;

@Beta
public final class ApplicationUpdatedEvent
    implements Event
{
    private final ApplicationEventType eventType;

    private final ApplicationKey applicationKey;

    public ApplicationUpdatedEvent( final ApplicationKey key, final ApplicationEventType eventType )
    {
        this.eventType = eventType;
        this.applicationKey = key;
    }

    public ApplicationEventType getEventType()
    {
        return eventType;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "eventType", this.eventType ).
            add( "applicationKey", this.applicationKey ).
            omitNullValues().
            toString();
    }
}
