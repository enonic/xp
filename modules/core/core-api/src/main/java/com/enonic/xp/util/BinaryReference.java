package com.enonic.xp.util;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public class BinaryReference
{
    private final String value;

    private BinaryReference( final String value )
    {
        this.value = value;
    }

    public static BinaryReference from( final String value )
    {
        Preconditions.checkArgument( !isNullOrEmpty( value ), "BinaryReference must not be null or empty" );
        return new BinaryReference( value );
    }

    @Override
    public String toString()
    {
        return this.value;
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

        final BinaryReference that = (BinaryReference) o;

        return value != null ? value.equals( that.value ) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}


