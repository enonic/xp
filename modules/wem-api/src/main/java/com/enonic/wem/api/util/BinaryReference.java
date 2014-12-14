package com.enonic.wem.api.util;

public class BinaryReference
{
    private final String name;

    private BinaryReference( final String name )
    {
        this.name = name;
    }

    public static BinaryReference from( final String value )
    {
        // TODO: deserialize
        return new BinaryReference( value );
    }

    @Override
    public String toString()
    {
        return this.name;
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

        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }
}


