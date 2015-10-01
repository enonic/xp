package com.enonic.wem.repo.internal.cache;

import java.util.Map;

import com.google.common.collect.Maps;

public class PathCacheImpl
    implements PathCache
{
    private final Map<CachePath, String> pathMap = Maps.newHashMap();

    private final Map<String, CachePath> idMap = Maps.newHashMap();

    @Override
    public void cache( final CachePath path, final String id )
    {
        final CachePath parentPath = path.getParentPath();

        doPut( path, id, parentPath );
    }

    private synchronized void doPut( final CachePath path, final String id, final CachePath parentPath )
    {
        final CachePath existingEntry = idMap.get( id );

        if ( existingEntry != null )
        {
            idMap.remove( id );
            pathMap.remove( existingEntry );
        }

        pathMap.put( path, id );
        idMap.put( id, path );
    }

    @Override
    public void evict( final CachePath nodePath )
    {
        doRemove( nodePath );
    }

    @Override
    public void evict( final String id )
    {
        doRemove( id );
    }

    private synchronized void doRemove( final String id )
    {
        final CachePath cachePath = idMap.get( id );
        idMap.remove( id );
        pathMap.remove( cachePath );
    }

    private synchronized void doRemove( final CachePath path )
    {
        final String id = pathMap.remove( path );
        idMap.remove( id );
    }

    @Override
    public String get( final CachePath path )
    {
        final String id = pathMap.get( path );

        return id;
    }
}
