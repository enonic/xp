package com.enonic.xp.module;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;

import com.enonic.xp.event.Event;

@Beta
public final class ModuleUpdatedEvent
    implements Event
{
    private final ModuleEventType eventType;

    private final ModuleKey moduleKey;

    public ModuleUpdatedEvent( final ModuleKey key, final ModuleEventType eventType )
    {
        this.eventType = eventType;
        this.moduleKey = key;
    }

    public ModuleEventType getEventType()
    {
        return eventType;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "eventType", this.eventType ).
            add( "moduleKey", this.moduleKey ).
            omitNullValues().
            toString();
    }
}
