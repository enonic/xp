package com.enonic.wem.api.item;


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
