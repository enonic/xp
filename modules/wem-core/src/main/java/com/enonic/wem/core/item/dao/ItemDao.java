package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;

public interface ItemDao
{
    public Item getItemById( ItemId id )
        throws NoItemWithIdFound;

    public Item getItemByPath( ItemPath path )
        throws NoItemFoundException;

    public void storeNew( Item item, ItemPath parent );

    public void updateExisting( final Item item );
}
