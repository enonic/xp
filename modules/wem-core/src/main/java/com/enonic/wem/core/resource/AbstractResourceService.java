package com.enonic.wem.core.resource;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceService;

public abstract class AbstractResourceService
    implements ResourceService
{
    @Override
    public final boolean hasResource( final ResourceKey key )
    {
        return resolve( key ) != null;
    }

    @Override
    public final Resource getResource( final ResourceKey key )
        throws ResourceNotFoundException
    {
        final Resource resource = resolve( key );
        if ( resource != null )
        {
            return resource;
        }

        throw new ResourceNotFoundException( key );
    }

    protected abstract Resource resolve( ResourceKey key );
}
