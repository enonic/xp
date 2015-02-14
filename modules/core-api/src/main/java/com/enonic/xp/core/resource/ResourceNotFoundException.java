package com.enonic.xp.core.resource;

import com.enonic.xp.core.exception.NotFoundException;

public final class ResourceNotFoundException
    extends NotFoundException
{
    private final ResourceKey resource;

    public ResourceNotFoundException( final ResourceKey resource )
    {
        super( "Resource [{0}] was not found", resource.getUri() );
        this.resource = resource;
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }
}
