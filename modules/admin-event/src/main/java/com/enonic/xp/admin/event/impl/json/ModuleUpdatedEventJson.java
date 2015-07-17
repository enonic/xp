package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.module.ModuleUpdatedEvent;

public final class ModuleUpdatedEventJson
    implements EventJson
{
    private final String eventType;

    private final String applicationKey;

    public ModuleUpdatedEventJson( final ModuleUpdatedEvent event )
    {
        this.eventType = event.getEventType().name();
        this.applicationKey = event.getApplicationKey().toString();
    }

    public String getEventType()
    {
        return eventType;
    }

    public String getApplicationKey()
    {
        return applicationKey;
    }
}
