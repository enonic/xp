package com.enonic.wem.api.entity;


public class NoEntityWithIdFoundException
    extends NoEntityFoundException
{
    private final EntityId id;

    public NoEntityWithIdFoundException( final EntityId id )
    {
        super( "No item with id " + id + " found" );
        this.id = id;
    }

    public EntityId getId()
    {
        return id;
    }
}
