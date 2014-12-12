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
}


