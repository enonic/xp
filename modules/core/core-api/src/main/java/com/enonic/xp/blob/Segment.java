package com.enonic.xp.blob;

public class Segment
{
    private final String value;

    private Segment( final String value )
    {
        this.value = value;
    }

    public static Segment from( final String value )
    {
        return new Segment( value );
    }

    public String getValue()
    {
        return value;
    }
}
