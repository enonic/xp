package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;

// TODO: Replace path type String with ItemPath/Path, when ItemPath is ready
public class ItemInMemoryDao
    implements ItemDao
{
    private final LinkedHashMap<ItemId, Item> itemByItemId = new LinkedHashMap<>();

    private final LinkedHashMap<String, ItemId> itemIdByPath = new LinkedHashMap<>();

    @Override
    public void storeNew( final Item item )
    {
        itemByItemId.put( item.id(), item );
        //itemIdByPath.put( item. )
    }

    public Item getItemById( final ItemId id )
    {
        return doGetItemById( id );
    }

    private Item doGetItemById( final ItemId id )
    {
        final Item item = this.itemByItemId.get( id );
        if ( item == null )
        {
            throw new NoItemWithIdFoundException( id );
        }
        return item;
    }

    public Item getItemByPath( final String path )
        throws NoItemFoundException
    {
        final ItemId id = this.itemIdByPath.get( path );
        if ( id == null )
        {
            throw new NoItemAtPathFoundException( path );
        }
        return doGetItemById( id );
    }
}
