package com.enonic.wem.api.entity;


import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Preconditions;

public class EntityId
{
    private final String value;

    public EntityId()
    {
        this.value = UUID.randomUUID().toString();
    }

    private EntityId( final String string )
    {
        Preconditions.checkNotNull( string, "string cannot be null" );
        this.value = string;
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

        final EntityId other = (EntityId) o;
        return Objects.equals( value, other.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }

    public String toString()
    {
        return value;
    }

    public static EntityId from( String string )
    {
        return new EntityId( string );
    }

    public static EntityId from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return new EntityId( object.toString() );
    }
}
