package com.enonic.xp.repo.impl.cache;

public interface PathCache
{
    void cache( final CachePath path, final String id );

    void evict( final CachePath path );

    void evict( final String id );

    String get( final CachePath path );
}
