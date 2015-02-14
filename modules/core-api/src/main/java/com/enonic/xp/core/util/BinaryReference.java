package com.enonic.xp.core.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class BinaryReference
{
    private final String value;

    private BinaryReference( final String value )
    {
        this.value = value;
    }

    public static BinaryReference from( final String value )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( value ), "BinaryReference must not be null or empty" );
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

        if ( value != null ? !value.equals( that.value ) : that.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}


