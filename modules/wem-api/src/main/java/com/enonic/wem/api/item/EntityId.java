package com.enonic.wem.api.item;


import java.util.UUID;

public class EntityId
{
    private final String value;

    public EntityId()
    {
        this.value = UUID.randomUUID().toString();
    }

    public EntityId( final String value )
    {
        this.value = value;
    }

    public EntityId( final Object value )
    {
        this.value = value.toString();
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

        final EntityId entityId = (EntityId) o;

        return value.equals( entityId.value );
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
