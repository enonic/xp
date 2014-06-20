package com.enonic.wem.core.resource;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceReference;
import com.enonic.wem.api.resource.ResourceService;

public abstract class AbstractResourceService
    implements ResourceService
{

    @Override
    public final Resource getResource( final ModuleResourceKey key )
        throws ResourceNotFoundException
    {
        final Resource resource = resolve( key );
        if ( resource != null )
        {
            return resource;
        }

        throw new ResourceNotFoundException( key );
    }

    @Override
    public final Resource getResource( final ResourceReference ref )
        throws ResourceNotFoundException
    {
        final Resource resource = resolve( ref );
        if ( resource != null )
        {
            return resource;
        }

        throw new ResourceNotFoundException( ref );
    }

    protected abstract Resource resolve( ModuleResourceKey key );

    protected abstract Resource resolve( ResourceReference ref );
}
