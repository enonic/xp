package com.enonic.wem.api.module;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.resource.Resource;

public final class CreateModuleResourceParams
{
    private ModuleResourceKey resourceKey;

    private Resource resource;

    public CreateModuleResourceParams resourceKey( final ModuleResourceKey value )
    {
        this.resourceKey = value;
        return this;
    }

    public CreateModuleResourceParams resource( final Resource resource )
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

    public void validate()
    {
        Preconditions.checkNotNull( this.resourceKey, "resourceKey cannot be null" );
        Preconditions.checkNotNull( this.resource, "resource cannot be null" );
    }
}
