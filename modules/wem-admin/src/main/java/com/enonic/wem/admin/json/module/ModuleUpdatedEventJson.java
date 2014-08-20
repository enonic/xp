package com.enonic.wem.admin.json.module;

import com.enonic.wem.admin.event.EventJson;
import com.enonic.wem.api.module.ModuleUpdatedEvent;

public final class ModuleUpdatedEventJson
    implements EventJson
{
    private final String state;

    private final String moduleKey;

    public ModuleUpdatedEventJson( final ModuleUpdatedEvent event )
    {
        this.state = event.getState().name();
        this.moduleKey = event.getModuleKey().toString();
    }

    public String getState()
    {
        return state;
    }

    public String getModuleKey()
    {
        return moduleKey;
    }
}
