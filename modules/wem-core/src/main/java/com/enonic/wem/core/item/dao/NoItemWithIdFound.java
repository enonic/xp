package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.ItemId;

public class NoItemWithIdFound
    extends NoItemFoundException
{
    private final ItemId id;

    NoItemWithIdFound( final ItemId id )
    {
        super( "No item with path " + id + " found" );
        this.id = id;
    }
}
