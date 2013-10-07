package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;

public final class GetModuleResource
    extends Command<Resource>
{
    private ModuleKey moduleKey;

    private String resourcePath; // ModulePath extends Path


    public GetModuleResource module( final ModuleKey moduleKey )
    {
        this.moduleKey = moduleKey;
        return this;
    }

    public GetModuleResource resourcePath( final String resourcePath )
    {
        this.resourcePath = resourcePath;
        return this;
    }

    String getResourcePath()
    {
        return resourcePath;
    }

    ModuleKey getModuleKey()
    {
        return moduleKey;
    }

    @Override
    public void validate()
    {
    }
}
