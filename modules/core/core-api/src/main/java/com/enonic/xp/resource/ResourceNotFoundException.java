package com.enonic.xp.resource;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;


public final class ResourceNotFoundException
    extends BaseException
{
    private final ResourceKey resource;

    public ResourceNotFoundException( final ResourceKey resource )
    {
        super( MessageFormat.format( "Resource [{0}] was not found", resource ) );
        this.resource = resource;
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }
}
