package com.enonic.xp.resource;

public class ResourceError
    extends Error
{
    private final ResourceKey resourceKey;

    public ResourceKey getResourceKey()
    {
        return resourceKey;
    }

    public ResourceError( final ResourceKey resourceKey, final String message )
    {
        super( message );
        this.resourceKey = resourceKey;
    }

    public ResourceError( final ResourceKey resourceKey, final String message, final Error cause )
    {
        super( message, cause );
        this.resourceKey = resourceKey;
    }
}
