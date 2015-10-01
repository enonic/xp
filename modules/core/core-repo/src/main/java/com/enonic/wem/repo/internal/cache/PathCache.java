package com.enonic.wem.repo.internal.cache;

public interface PathCache
{
    void cache( final CachePath path, final String id );

    void evict( final CachePath path );

    String get( final CachePath path );
}
