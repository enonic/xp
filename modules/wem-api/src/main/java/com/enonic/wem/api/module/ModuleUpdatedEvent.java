package com.enonic.wem.api.module;

import com.google.common.base.Objects;

import com.enonic.wem.api.event.Event;

public final class ModuleUpdatedEvent
    implements Event
{
    private final ModuleState state;

    private final ModuleKey moduleKey;

    public ModuleUpdatedEvent( final ModuleKey key, final ModuleState state )
    {
        this.state = state;
        this.moduleKey = key;
    }

    public ModuleState getState()
    {
        return state;
    }

    public ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "state", this.state ).
            add( "moduleKey", this.moduleKey ).
            omitNullValues().
            toString();
    }
}
