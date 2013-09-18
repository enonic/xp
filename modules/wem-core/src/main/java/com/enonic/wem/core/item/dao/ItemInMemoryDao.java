package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

public class ItemInMemoryDao
    implements ItemDao
{
    private final ItemIdByPath itemIdByPath;

    private final ItemByItemId itemByItemId;

    public ItemInMemoryDao()
    {
        itemByItemId = new ItemByItemId( new LinkedHashMap<ItemId, Item>() );
        itemIdByPath = new ItemIdByPath( new LinkedHashMap<ItemPath, ItemId>() );
    }

    @Override
    public void storeNew( final Item item, ItemPath parent )
    {
        Preconditions.checkArgument( parent.isAbsolute(), "Path to parent Item must be absolute: " + parent.toString() );
        if ( !itemIdByPath.pathHasItem( parent ) )
        {
            throw new IllegalArgumentException( "Parent Item does not exist", new NoItemAtPathFound( parent ) );
        }

        final ItemPath path = new ItemPath( parent, item.name() );
        itemByItemId.storeNew( item );
        itemIdByPath.put( path, item.id() );
    }

    @Override
    public void updateExisting( final Item item )
    {
        itemByItemId.updateExisting( item );
    }

    public Item getItemById( final ItemId id )
    {
        return this.itemByItemId.get( id );
    }

    public Item getItemByPath( final ItemPath path )
        throws NoItemFoundException
    {
        Preconditions.checkArgument( path.isAbsolute(), "path must be absolute: " + path.toString() );

        return this.itemByItemId.get( itemIdByPath.get( path ) );
    }
}
