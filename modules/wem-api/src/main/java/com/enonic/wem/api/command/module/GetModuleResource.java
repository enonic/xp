package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.resource.Resource;

public final class GetModuleResource
    extends Command<Resource>
{
    private ModuleVersion moduleVersion;

    private String resourcePath; // ModulePath extends Path


    public GetModuleResource module( final ModuleVersion moduleVersion )
    {
        this.moduleVersion = moduleVersion;
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

    ModuleVersion getModuleVersion()
    {
        return moduleVersion;
    }

    @Override
    public void validate()
    {
    }
}
