package com.enonic.wem.api.resource;

import com.enonic.wem.api.NotFoundException;

public final class Resource2NotFoundException
    extends NotFoundException
{
    private final ResourceKey key;

    public Resource2NotFoundException( final ResourceKey key )
    {
        super( "Resource [{0}] was not found", key.toString() );
        this.key = key;
    }

    public ResourceKey getResource()
    {
        return this.key;
    }
}
