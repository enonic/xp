package com.enonic.wem.repo.internal.cache;

import com.google.common.collect.ImmutableSet;

public interface PathCache
{
    void cache( final CachePath path, final String id );

    void evict( final CachePath path );

    void evict( final String id );

    String get( final CachePath path );


    ImmutableSet<String> getChildren( final CachePath path );
}
