package com.enonic.xp.query.highlight.constants;

public enum Fragmenter
{
    SIMPLE( "simple" ), SPAN( "span" );

    private final String value;

    Fragmenter( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static Fragmenter from( final String state )
    {
        if ( SIMPLE.value().equals( state ) )
        {
            return SIMPLE;
        }
        if ( SPAN.value().equals( state ) )
        {
            return SPAN;
        }

        return null;
    }
}
