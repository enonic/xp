package com.enonic.xp.lib.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

public final class CacheBeanBuilder
{
    private final CacheBuilder<Object, Object> builder;

    public CacheBeanBuilder()
    {
        this.builder = CacheBuilder.newBuilder();
    }

    public void setSize( final int size )
    {
        this.builder.maximumSize( size );
    }


    public void setExpire( final int expire )
    {
        this.builder.expireAfterWrite( expire, TimeUnit.SECONDS );
    }

    public CacheBean build()
    {
        return new CacheBean( this.builder.build() );
    }
}
