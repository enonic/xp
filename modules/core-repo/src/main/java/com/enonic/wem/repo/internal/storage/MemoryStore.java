package com.enonic.wem.repo.internal.storage;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.node.NodePath;

public class MemoryStore
{

    private Map<String, StorageData> idEntryMap = Maps.newHashMap();

    private Map<NodePath, String> pathEntryMap = Maps.newHashMap();


    public void put( final String id, final NodePath path, final StorageData data )
    {
        this.idEntryMap.put( id, data );
        this.pathEntryMap.put( path, id );
    }

    public void remove( final String id )
    {
        idEntryMap.remove( id );

    }

    public StorageData getById( final String id )
    {
        return idEntryMap.get( id );
    }

    public StorageData getByPath( final NodePath path )
    {
        final String id = pathEntryMap.get( path );
        return this.idEntryMap.get( id );
    }

}
