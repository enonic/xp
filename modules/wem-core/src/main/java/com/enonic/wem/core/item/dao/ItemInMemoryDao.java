package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemAlreadyExist;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.item.NoItemAtPathFound;
import com.enonic.wem.api.item.NoItemFoundException;

import static com.enonic.wem.api.item.Item.newItem;

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
    public Item createItem( final CreateItemArgs createItemArgs )
    {
        Preconditions.checkArgument( createItemArgs.parent().isAbsolute(),
                                     "Path to parent Item must be absolute: " + createItemArgs.parent().toString() );
        if ( !itemIdByPath.pathHasItem( createItemArgs.parent() ) )
        {
            throw new NoItemAtPathFound( createItemArgs.parent() );
        }

        final Item newItem = newItem().
            id( new ItemId() ).
            createdTime( DateTime.now() ).
            creator( createItemArgs.creator() ).
            parent( createItemArgs.parent() ).
            name( createItemArgs.name() ).
            icon( createItemArgs.icon() ).
            rootDataSet( createItemArgs.rootDataSet() ).
            build();

        if ( itemIdByPath.pathHasItem( newItem.path() ) )
        {
            throw new ItemAlreadyExist( newItem.path() );
        }

        itemByItemId.storeNew( newItem );
        itemIdByPath.put( newItem.path(), newItem.id() );
        return newItem;
    }

    @Override
    public Item updateItem( final UpdateItemArgs updateItemArgs )
    {
        final Item existing = itemByItemId.get( updateItemArgs.itemToUpdate() );

        final Item persistedItem = newItem( existing ).
            modifiedTime( DateTime.now() ).
            modifier( updateItemArgs.updater() ).
            name( updateItemArgs.name() ).
            icon( updateItemArgs.icon() ).
            rootDataSet( updateItemArgs.rootDataSet() ).
            build();

        itemByItemId.updateExisting( persistedItem );
        return persistedItem;
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
