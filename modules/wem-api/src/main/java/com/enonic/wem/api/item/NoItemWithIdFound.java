package com.enonic.wem.api.item;


public class NoItemWithIdFound
    extends NoItemFoundException
{
    private final ItemId id;

    public NoItemWithIdFound( final ItemId id )
    {
        super( "No item with id " + id + " found" );
        this.id = id;
    }
}
