package com.enonic.wem.api.command.module;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public final class CreateModuleResource
    extends Command<Resource>
{
    private ModuleResourceKey resourceKey;

    private Resource resource;

    public CreateModuleResource resourceKey( final ModuleResourceKey value )
    {
        this.resourceKey = value;
        return this;
    }

    public CreateModuleResource resource( final Resource resource )
    {
        this.resource = resource;
        return this;
    }

    public ModuleResourceKey getResourceKey()
    {
        return resourceKey;
    }

    public Resource getResource()
    {
        return resource;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.resourceKey, "resourceKey cannot be null" );
        Preconditions.checkNotNull( this.resource, "resource cannot be null" );
    }
}
