package com.enonic.xp.core.data;

import java.util.Objects;

public final class PropertyId
{
    private final String value;

    PropertyId( final String value )
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

        final PropertyId other = (PropertyId) o;
        return Objects.equals( value, other.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( value );
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
