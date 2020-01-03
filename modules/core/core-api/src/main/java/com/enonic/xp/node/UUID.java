package com.enonic.xp.node;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class UUID
{
    protected final String value;

    private static final String VALID_NODE_ID_PATTERN = "([a-z0-9A-Z_\\-\\.:])*";

    public UUID()
    {
        this.value = java.util.UUID.randomUUID().toString();
    }

    protected UUID( final String value )
    {
        Preconditions.checkNotNull( value, "UUID cannot be null" );
        Preconditions.checkArgument( !value.trim().isEmpty(), "UUID cannot be blank" );
        Preconditions.checkArgument( value.matches( "^" + VALID_NODE_ID_PATTERN + "$" ), "UUID format incorrect: " + value );

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

        final UUID other = (UUID) o;
        return Objects.equals( value, other.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value );
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static UUID from( String string )
    {
        return new UUID( string );
    }

    public static UUID from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return new UUID( object.toString() );
    }
}
