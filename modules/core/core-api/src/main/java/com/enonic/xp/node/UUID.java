package com.enonic.xp.node;


import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class UUID
{
    protected final String value;

    private static final Pattern VALID_NODE_ID_PATTERN = Pattern.compile( "[\\w\\-.:]+" );

    public UUID()
    {
        this.value = java.util.UUID.randomUUID().toString();
    }

    protected UUID( final String value )
    {
        this.value = check( value );
    }

    protected UUID( final Object object )
    {
        if ( object instanceof UUID )
        {
            this.value = ( (UUID) object ).value;
        }
        else
        {
            Preconditions.checkNotNull( object, "object cannot be null" );
            this.value = check( object.toString() );
        }
    }

    private static String check( String value )
    {
        Preconditions.checkNotNull( value, "UUID cannot be null" );
        Preconditions.checkArgument( !value.isBlank(), "UUID cannot be blank" );
        Preconditions.checkArgument( VALID_NODE_ID_PATTERN.matcher( value ).matches(), "UUID format incorrect: " + value );
        return value;
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
        final UUID uuid = (UUID) o;
        return value.equals( uuid.value );
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

    @Deprecated
    public static UUID from( final String string )
    {
        return new UUID( string );
    }

    @Deprecated
    public static UUID from( final Object object )
    {
        return new UUID( object );
    }
}
