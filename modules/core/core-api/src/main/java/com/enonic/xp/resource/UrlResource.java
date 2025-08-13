package com.enonic.xp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.google.common.base.Optional;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UrlResource
    extends ResourceBase
{
    private final URL url;

    private final String resolverName;

    public UrlResource( final ResourceKey key, final URL url )
    {
        super( key );
        this.url = url;
        this.resolverName = null;
    }

    public UrlResource( final ResourceKey key, final URL url, final String resolverName )
    {
        super( key );
        this.url = url;
        this.resolverName = resolverName;
    }

    @Override
    public boolean exists()
    {
        try
        {
            openConnectionForMetadata();
            return true;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    @Override
    public long getSize()
    {
        try
        {
            return openConnectionForMetadata().getContentLengthLong();
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }

    @Override
    public long getTimestamp()
    {
        try
        {
            return openConnectionForMetadata().getLastModified();
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }

    private URLConnection openConnectionForMetadata()
        throws IOException
    {
        final URLConnection urlConnection = this.url.openConnection();
        // https://bugs.openjdk.org/browse/JDK-6956385
        urlConnection.getInputStream().close();
        return urlConnection;
    }

    @Override
    public ByteSource getBytes()
    {
        requireExists();
        return new UrlResourceByteSource();
    }

    @Override
    public String getResolverName()
    {
        return resolverName;
    }

    private final class UrlResourceByteSource
        extends ByteSource
    {
        @Override
        public InputStream openStream()
            throws IOException
        {
            return url.openStream();
        }

        @Override
        public Optional<Long> sizeIfKnown()
        {
            try
            {
                return Optional.of( openConnectionForMetadata().getContentLengthLong() );
            }
            catch ( Exception e )
            {
                return Optional.absent();
            }
        }
    }
}
