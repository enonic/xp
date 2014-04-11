package com.enonic.wem.core.resource;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;

final class Resource2Impl
    implements Resource2
{
    private final ResourceKey key;

    private final URL resolvedUrl;

    public Resource2Impl( final ResourceKey key, final URL resolvedUrl )
    {
        this.key = key;
        this.resolvedUrl = resolvedUrl;
    }

    @Override
    public ResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public URL getResolvedUrl()
    {
        return this.resolvedUrl;
    }

    @Override
    public long getSize()
    {
        try
        {
            return this.resolvedUrl.openConnection().getContentLength();
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }

    @Override
    public long getTimestamp()
    {
        try
        {
            return this.resolvedUrl.openConnection().getLastModified();
        }
        catch ( final Exception e )
        {
            return 0;
        }
    }

    @Override
    public ByteSource getByteSource()
    {
        return Resources.asByteSource( this.resolvedUrl );
    }

    @Override
    public String getAsString()
    {
        try
        {
            return getByteSource().asCharSource( Charsets.UTF_8 ).read();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }
}
