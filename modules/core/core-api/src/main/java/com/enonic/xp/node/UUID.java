package com.enonic.xp.node;


import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class UUID
{
    protected final String value;

    private static final Pattern VALID_NODE_ID_PATTERN = Pattern.compile( "^(?:[a-zA-Z0-9_\\-.:])+$" );

    public UUID()
    {
        this.value = java.util.UUID.randomUUID().toString();
    }

    protected UUID( final String value )
    {
        Preconditions.checkNotNull( value, "UUID cannot be null" );
        Preconditions.checkArgument( !value.isBlank(), "UUID cannot be blank" );
        Preconditions.checkArgument( VALID_NODE_ID_PATTERN.matcher( value ).matches(), "UUID format incorrect: " + value );

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
        return value.hashCode();
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
