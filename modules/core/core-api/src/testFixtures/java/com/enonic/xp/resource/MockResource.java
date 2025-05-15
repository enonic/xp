package com.enonic.xp.resource;

import com.google.common.io.ByteSource;

public class MockResource
    extends ResourceBase
{
    private final byte[] bytes;

    private final long timestamp;

    public MockResource( final ResourceKey key, final byte[] bytes, final long timestamp )
    {
        super( key );
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    public static MockResource missing( final ResourceKey key )
    {
        return new MockResource( key, null, 0 );
    }

    public static MockResource empty( final ResourceKey key, long timestamp )
    {
        return new MockResource( key, new byte[]{}, timestamp );
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
    public String getResolverName()
    {
        return "test";
    }

}
