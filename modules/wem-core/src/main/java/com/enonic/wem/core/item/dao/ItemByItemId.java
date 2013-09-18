package com.enonic.wem.core.item.dao;

import java.util.Map;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;

class ItemByItemId
{
    private final Map<ItemId, Item> itemByItemId;

    ItemByItemId( Map<ItemId, Item> itemByItemId )
    {
        this.itemByItemId = itemByItemId;
    }

    public void storeNew( final Item item )
    {
        itemByItemId.put( item.id(), item );
    }

    public void updateExisting( final Item item )
    {
        if ( !itemByItemId.containsKey( item.id() ) )
        {
            throw new NoItemWithIdFound( item.id() );
        }
        itemByItemId.put( item.id(), item );
    }

    public boolean containsKey( final ItemId id )
    {
        return itemByItemId.containsKey( id );
    }

    public Item get( final ItemId id )
    {
        final Item item = itemByItemId.get( id );
        if ( item == null )
        {
            throw new NoItemWithIdFound( id );
        }
        return item;
    }
}
