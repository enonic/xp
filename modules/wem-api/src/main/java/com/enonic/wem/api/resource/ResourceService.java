package com.enonic.wem.api.resource;

public interface ResourceService
{
    public Resource getResource( ResourceKey key )
        throws ResourceNotFoundException;
}
