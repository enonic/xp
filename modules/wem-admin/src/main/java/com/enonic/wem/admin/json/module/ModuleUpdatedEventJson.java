package com.enonic.wem.admin.json.module;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.module.ModuleUpdatedEvent;

public final class ModuleUpdatedEventJson
    implements EventJson
{
    private final String eventType;

    private final String moduleKey;

    public ModuleUpdatedEventJson( final ModuleUpdatedEvent event )
    {
        this.eventType = event.getEventType().name();
        this.moduleKey = event.getModuleKey().toString();
    }

    public String getEventType()
    {
        return eventType;
    }

    public String getModuleKey()
    {
        return moduleKey;
    }
}
