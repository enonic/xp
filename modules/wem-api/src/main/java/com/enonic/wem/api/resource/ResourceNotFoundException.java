package com.enonic.wem.api.resource;

import com.enonic.wem.api.exception.NotFoundException;

public final class ResourceNotFoundException
    extends NotFoundException
{
    private final ResourceReference ref;

    public ResourceNotFoundException( final ResourceReference ref )
    {
        super( "Resource [{0}] was not found", ref.getUri() );
        this.ref = ref;
    }

    public ResourceReference getResource()
    {
        return this.ref;
    }
}
