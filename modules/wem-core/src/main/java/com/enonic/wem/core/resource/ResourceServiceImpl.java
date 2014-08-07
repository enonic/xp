package com.enonic.wem.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.api.resource.ResourceService;
import com.enonic.wem.api.resource.ResourceUrlResolver;

public final class ResourceServiceImpl
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

    private Resource resolve( final ModuleResourceKey key )
    {
        final URL resourceUrl = ResourceUrlResolver.resolve( key );
        if ( !isResource( resourceUrl ) )
        {
            return null;
        }

        return new ResourceImpl( key ).
            byteSource( Resources.asByteSource( resourceUrl ) ).
            timestamp( getResourceTimeStamp( resourceUrl ) );
    }

    // TODO Hack to check if resource pointed to by URL exists
    private boolean isResource( final URL resourceUrl )
    {
        try
        {
            final InputStream stream = resourceUrl.openStream();
            stream.close();
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    private long getResourceTimeStamp( final URL resourceUrl )
    {
        try
        {
            return resourceUrl.openConnection().getLastModified();
        }
        catch ( IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public URL resolveUrl( final ModuleResourceKey key )
    {
        return null;
    }
}
