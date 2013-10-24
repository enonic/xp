package com.enonic.wem.api.entity;


public class NoEntityWithIdFound
    extends NoEntityFoundException
{
    private final EntityId id;

    public NoEntityWithIdFound( final EntityId id )
    {
        super( "No item with id " + id + " found" );
        this.id = id;
    }
}
