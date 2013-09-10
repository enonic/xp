package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;

public interface ItemDao
{
    public Item getItemById( final ItemId id )
        throws NoItemWithIdFoundException;

    public Item getItemByPath( final String path )
        throws NoItemFoundException;

    public void storeNew( Item item );
}
