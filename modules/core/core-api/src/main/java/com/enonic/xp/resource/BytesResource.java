package com.enonic.xp.resource;

import java.net.URL;

import com.google.common.io.ByteSource;

@Deprecated
public final class BytesResource
    extends ResourceBase
{
    private final byte[] bytes;

    private final long timestamp;

    public BytesResource( final ResourceKey key, final byte[] bytes )
    {
        super( key );
        this.bytes = bytes;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public URL getUrl()
    {
        return null;
    }

    @Override
    public boolean exists()
    {
        return this.bytes != null;
    }

    @Override
    public long getSize()
    {
        return this.bytes.length;
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public ByteSource getBytes()
    {
        requireExists();
        return ByteSource.wrap( this.bytes );
    }

    @Override
    public ResourceResolver getResolver()
    {
        return null;
    }
}
