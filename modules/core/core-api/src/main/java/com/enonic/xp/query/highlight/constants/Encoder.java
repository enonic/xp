package com.enonic.xp.query.highlight.constants;

public enum Encoder
{
    DEFAULT( "default" ), HTML( "html" );

    private final String value;

    Encoder( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static Encoder from( final String state )
    {
        if ( DEFAULT.value().equals( state ) )
        {
            return DEFAULT;
        }
        if ( HTML.value().equals( state ) )
        {
            return HTML;
        }

        return null;
    }
}
