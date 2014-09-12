package com.enonic.wem.api.module;

import com.google.common.base.Objects;

import com.enonic.wem.api.event.Event;

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
        return Objects.toStringHelper( this ).
            add( "eventType", this.eventType ).
            add( "moduleKey", this.moduleKey ).
            omitNullValues().
            toString();
    }
}
