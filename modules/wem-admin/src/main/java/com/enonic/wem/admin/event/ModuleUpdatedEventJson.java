package com.enonic.wem.admin.event;

import com.enonic.wem.api.module.ModuleUpdatedEvent;

final class ModuleUpdatedEventJson
    implements EventJson
{
    private final String state;

    private final String moduleKey;

    ModuleUpdatedEventJson( final ModuleUpdatedEvent event )
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
