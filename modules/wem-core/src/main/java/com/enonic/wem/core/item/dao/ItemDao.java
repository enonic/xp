package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.item.NoItemFoundException;
import com.enonic.wem.api.item.NoItemWithIdFound;

public interface ItemDao
{
    public Item getItemById( ItemId id )
        throws NoItemWithIdFound;

    public Item getItemByPath( ItemPath path )
        throws NoItemFoundException;

    public Item createItem( CreateItemArgs createItemArgs );

    public Item updateItem( final UpdateItemArgs updateItemArgs );
}
