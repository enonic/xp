package com.enonic.wem.api.resource;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class ResourceNotFoundException
    extends NotFoundException
{
    private final ModuleResourceKey resource;

    public ResourceNotFoundException( final ModuleResourceKey resource )
    {
        super( "Resource [{0}] was not found", resource.getUri() );
        this.resource = resource;
    }

    public ModuleResourceKey getResource()
    {
        return this.resource;
    }
}
