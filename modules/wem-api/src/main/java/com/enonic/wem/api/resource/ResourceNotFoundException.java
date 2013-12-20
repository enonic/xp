package com.enonic.wem.api.resource;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.module.ResourcePath;

public class ResourceNotFoundException
    extends NotFoundException
{
    private ResourcePath resourcePath;

    public ResourceNotFoundException( final ResourcePath resourcePath )
    {
        super( "Resource [{0}] was not found", resourcePath );
        this.resourcePath = resourcePath;
    }

    public ResourceNotFoundException( final String path )
    {
        super( "Resource [{0}] was not found", path );
    }

    public ResourcePath getResourcePath()
    {
        return resourcePath;
    }
}
