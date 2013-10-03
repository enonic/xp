package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.ItemPath;

public class ItemAlreadyExist
    extends RuntimeException
{
    private ItemPath item;

    public ItemAlreadyExist( final ItemPath item )
    {
        super( "Item already exist: " + item );
        this.item = item;
    }

    public ItemPath getItem()
    {
        return item;
    }
}
