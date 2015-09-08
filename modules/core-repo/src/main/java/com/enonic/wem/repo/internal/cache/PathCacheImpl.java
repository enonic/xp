package com.enonic.wem.repo.internal.cache;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class PathCacheImpl
    implements PathCache
{
    private final Multimap<CachePath, String> childMap = HashMultimap.create();

    private final Map<CachePath, String> pathMap = Maps.newHashMap();

    private final Map<String, CachePath> idMap = Maps.newHashMap();

    @Override
    public void put( final CachePath path, final String id )
    {
        final CachePath parentPath = path.getParentPath();

        doPut( path, id, parentPath );
    }

    private synchronized void doPut( final CachePath path, final String id, final CachePath parentPath )
    {
        pathMap.put( path, id );

        // Remove from other parent if any
        final CachePath existingPath = idMap.get( id );

        if ( existingPath != null )
        {
            childMap.remove( existingPath.getParentPath(), id );
        }

        idMap.put( id, path );
        childMap.put( parentPath, id );
    }

    @Override
    public void remove( final CachePath nodePath )
    {
        doRemove( nodePath );
    }

    @Override
    public void remove( final String id )
    {
        doRemove( id );
    }

    private synchronized void doRemove( final String id )
    {
        final CachePath cachePath = idMap.get( id );
        idMap.remove( id );
        childMap.remove( cachePath, id );
        pathMap.remove( cachePath );
    }

    private synchronized void doRemove( final CachePath path )
    {
        final String id = pathMap.remove( path );
        childMap.remove( path.getParentPath(), id );
        idMap.remove( id );
    }

    @Override
    public String get( final CachePath path )
    {
        return pathMap.get( path );
    }

    @Override
    public Collection<String> getChildren( final CachePath path )
    {
        return childMap.get( path );
    }

}
