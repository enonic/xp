package com.enonic.wem.portal.script.runner;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;

final class ScriptSource
{
    private final Resource resource;

    public ScriptSource( final Resource resource )
    {
        this.resource = resource;
    }

    public String getName()
    {
        return this.resource.getKey().toString();
    }

    public String getScriptAsString()
    {
        return this.resource.readString();
    }

    public long getTimestamp()
    {
        return this.resource.getTimestamp();
    }

    public ModuleKey getModule()
    {
        return this.resource.getKey().getModule();
    }

    public ResourceKey getResource()
    {
        return this.resource.getKey();
    }

    public String toString()
    {
        return getName();
    }
}
