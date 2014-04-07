package com.enonic.wem.core.module.source;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleResourceKey;

public final class ModuleSourceImpl
    implements ModuleSource
{
    private final ModuleResourceKey key;

    private final URL resolvedUrl;

    public ModuleSourceImpl( final ModuleResourceKey key, final URL resolvedUrl )
    {
        this.key = key;
        this.resolvedUrl = resolvedUrl;
    }

    @Override
    public String getUri()
    {
        return this.key.toString();
    }

    @Override
    public ModuleResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public boolean exists()
    {
        return this.resolvedUrl != null;
    }

    @Override
    public URL getResolvedUrl()
    {
        return this.resolvedUrl;
    }

    @Override
    public ByteSource getBytes()
    {
        if ( this.resolvedUrl != null )
        {
            return Resources.asByteSource( this.resolvedUrl );
        }

        throw new SourceNotFoundException( this.key );
    }

    @Override
    public long getTimestamp()
    {
        if ( this.resolvedUrl == null )
        {
            return 0;
        }

        try
        {
            return this.resolvedUrl.openConnection().getLastModified();
        }
        catch ( final IOException e )
        {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        return getUri();
    }
}
