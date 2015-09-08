package com.enonic.wem.repo.internal.cache;

import java.util.Collection;

public interface PathCache
{
    void put( final CachePath path, final String id );

    void remove( final CachePath path );

    void remove( final String id );

    String get( final CachePath path );

    Collection<String> getChildren( final CachePath path );
}
