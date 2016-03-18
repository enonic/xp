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

        final Segment segment = (Segment) o;

        return value != null ? value.equals( segment.value ) : segment.value == null;

    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}
