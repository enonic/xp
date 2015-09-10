package com.enonic.wem.repo.internal.cache;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class PathCacheImpl
    implements PathCache
{
    private final Multimap<CachePath, String> childMap = HashMultimap.create();

    private final Map<CachePath, String> pathMap = Maps.newHashMap();

    private final Map<String, CachePath> idMap = Maps.newHashMap();

    private final Logger LOG = LoggerFactory.getLogger( PathCacheImpl.class );

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
            childMap.remove( existingEntry, id );
        }

        pathMap.put( path, id );
        idMap.put( id, path );
        childMap.put( parentPath, id );
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
        final String id = pathMap.get( path );

        LOG.info( "Cache result for path: '" + path + "', id: " + id );

        return id;
    }

    @Override
    public CachePath get( final String id )
    {
        return idMap.get( id );
    }

    @Override
    public ImmutableSet<String> getChildren( final CachePath path )
    {
        return ImmutableSet.copyOf( childMap.get( path ) );
    }

}
