package com.enonic.xp.util;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class BinaryReference
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
        return this == o || o instanceof BinaryReference && value.equals( ( (BinaryReference) o ).value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}


