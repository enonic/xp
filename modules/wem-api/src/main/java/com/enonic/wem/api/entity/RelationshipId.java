package com.enonic.wem.api.entity;


import java.util.UUID;

public class RelationshipId
{
    private final String value;

    public RelationshipId()
    {
        this.value = UUID.randomUUID().toString();
    }

    public RelationshipId( final String value )
    {
        this.value = value;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final RelationshipId itemId = (RelationshipId) o;

        return value.equals( itemId.value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    public String toString()
    {
        return value;
    }
}
