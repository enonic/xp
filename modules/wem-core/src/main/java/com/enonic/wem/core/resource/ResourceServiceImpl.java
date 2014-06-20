package com.enonic.wem.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.inject.Inject;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleResourceUrlResolver;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceReference;
import com.enonic.wem.core.config.SystemConfig;

public final class ResourceServiceImpl
    extends AbstractResourceService
{
    protected final SystemConfig systemConfig;

    @Inject
    public ResourceServiceImpl( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Override
    protected Resource resolve( final ModuleResourceKey key )
    {
        final URL resourceUrl = ModuleResourceUrlResolver.resolve( key );
        if (!isResource(resourceUrl)) {
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
    protected Resource resolve( final ResourceReference ref )
    {
        return null;
    }
}
