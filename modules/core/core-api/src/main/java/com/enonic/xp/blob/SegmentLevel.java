package com.enonic.xp.blob;

public final class SegmentLevel
{
    private final String value;

    private SegmentLevel( final String value )
    {
        this.value = value;
    }

    public static SegmentLevel from( final String value )
    {
        return new SegmentLevel( value );
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

        final SegmentLevel segment = (SegmentLevel) o;

        return value != null ? value.equals( segment.value ) : segment.value == null;

    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }
}
