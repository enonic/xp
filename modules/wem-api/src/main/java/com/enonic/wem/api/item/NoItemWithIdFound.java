package com.enonic.wem.api.item;


public class NoItemWithIdFound
    extends NoItemFoundException
{
    private final EntityId id;

    public NoItemWithIdFound( final EntityId id )
    {
        super( "No item with id " + id + " found" );
        this.id = id;
    }
}
