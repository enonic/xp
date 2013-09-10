package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.ItemId;

public class NoItemWithIdFoundException
    extends NoItemFoundException
{
    private final ItemId id;

    NoItemWithIdFoundException( final ItemId id )
    {
        super( "No item with path " + id + " found" );
        this.id = id;
    }
}
