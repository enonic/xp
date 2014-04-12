package com.enonic.wem.api.resource;

public interface ResourceService
{
    public boolean hasResource( ResourceKey key );

    public Resource2 getResource( ResourceKey key )
        throws Resource2NotFoundException;

    public ResourceKeys getChildren( ResourceKey parent );
}
