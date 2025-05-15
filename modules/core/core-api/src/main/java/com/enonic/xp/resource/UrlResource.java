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
            openConnection();
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
            return openConnection().getContentLengthLong();
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
            return openConnection().getLastModified();
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }

    private URLConnection openConnection()
    {
        if ( this.url == null )
        {
            throw new ResourceNotFoundException( getKey() );
        }

        try
        {
            final URLConnection connection = this.url.openConnection();
            if ( connection == null )
            {
                throw new ResourceNotFoundException( getKey() );
            }

            connection.connect();
            return connection;
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
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
                final URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                return Optional.of( urlConnection.getContentLengthLong() );
            }
            catch ( Exception e )
            {
                return Optional.absent();
            }
        }
    }
}
