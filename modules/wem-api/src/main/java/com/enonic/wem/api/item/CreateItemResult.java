package com.enonic.wem.api.item;


public class CreateItemResult
{
    private final Item persistedItem;

    public CreateItemResult( final Item persistedItem )
    {
        this.persistedItem = persistedItem;
    }

    public Item getPersistedItem()
    {
        return persistedItem;
    }
}
