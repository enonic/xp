package com.enonic.wem.core.item.dao;

import java.util.List;
import java.util.Map;

import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

class ItemIdByPath
{
    private final Map<ItemPath, ItemId> itemIdByPath;

    ItemIdByPath( Map<ItemPath, ItemId> itemIdByPath )
    {
        this.itemIdByPath = itemIdByPath;
    }

    boolean pathHasItem( final ItemPath path )
    {
        final List<ItemPath> parentPaths = path.getParentPaths();
        for ( int i = parentPaths.size() - 1; i >= 0; i-- )
        {
            final ItemPath parentPath = parentPaths.get( i );
            if ( !parentPath.isRoot() && !itemIdByPath.containsKey( parentPath ) )
            {
                return false;
            }
        }

        return path.isRoot() || itemIdByPath.containsKey( path );
    }

    void put( final ItemPath path, final ItemId id )
    {
        itemIdByPath.put( path, id );
    }

    ItemId get( final ItemPath path )
    {
        final ItemId id = itemIdByPath.get( path );
        if ( id == null )
        {
            throw new NoItemAtPathFound( path );
        }
        return id;
    }
}