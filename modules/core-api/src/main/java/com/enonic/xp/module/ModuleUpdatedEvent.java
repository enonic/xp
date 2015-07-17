package com.enonic.xp.module;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.app.ApplicationEventType;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;

@Beta
public final class ModuleUpdatedEvent
    implements Event
{
    private final ApplicationEventType eventType;

    private final ApplicationKey applicationKey;

    public ModuleUpdatedEvent( final ApplicationKey key, final ApplicationEventType eventType )
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
