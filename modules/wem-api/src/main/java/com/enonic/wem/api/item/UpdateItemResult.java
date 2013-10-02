package com.enonic.wem.api.item;


public class UpdateItemResult
{
    private final Item persistedItem;

    public UpdateItemResult( final Item persistedItem )
    {
        this.persistedItem = persistedItem;
    }

    public Item getPersistedItem()
    {
        return persistedItem;
    }
}
