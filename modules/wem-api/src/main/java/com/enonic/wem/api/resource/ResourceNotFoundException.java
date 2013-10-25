package com.enonic.wem.api.resource;

import com.enonic.wem.api.exception.BaseException;
import com.enonic.wem.api.module.ResourcePath;

public class ResourceNotFoundException
    extends BaseException
{
    private final ResourcePath resourcePath;

    public ResourceNotFoundException( final ResourcePath resourcePath )
    {
        super( "Resource [{0}] was not found", resourcePath );
        this.resourcePath = resourcePath;
    }

    public ResourcePath getResourcePath()
    {
        return resourcePath;
    }
}
