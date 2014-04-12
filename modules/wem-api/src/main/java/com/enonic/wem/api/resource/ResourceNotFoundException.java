package com.enonic.wem.api.resource;

import com.enonic.wem.api.NotFoundException;

public final class ResourceNotFoundException
    extends NotFoundException
{
    private final ResourceKey key;

    public ResourceNotFoundException( final ResourceKey key )
    {
        super( "Resource [{0}] was not found", key.toString() );
        this.key = key;
    }

    public ResourceKey getResource()
    {
        return this.key;
    }
}
