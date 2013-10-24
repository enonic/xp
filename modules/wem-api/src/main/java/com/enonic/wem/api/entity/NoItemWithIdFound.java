package com.enonic.wem.api.entity;


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
