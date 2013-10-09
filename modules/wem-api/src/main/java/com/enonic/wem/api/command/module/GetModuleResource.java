package com.enonic.wem.api.command.module;


import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleResourcePath;
import com.enonic.wem.api.resource.Resource;

public final class GetModuleResource
    extends Command<Resource>
{
    private ModuleResourcePath resourcePath;

    public GetModuleResource resourcePath( final ModuleResourcePath value )
    {
        this.resourcePath = value;
        return this;
    }

    public ModuleResourcePath getResourcePath()
    {
        return resourcePath;
    }

    @Override
    public void validate()
    {
    }
}
