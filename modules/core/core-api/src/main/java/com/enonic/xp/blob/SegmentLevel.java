package com.enonic.xp.blob;

import java.util.Objects;

public final class SegmentLevel
{
    private final String value;

    SegmentLevel( final String value )
    {
        this.value = Objects.requireNonNull( value );
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
        return ( o instanceof SegmentLevel ) && this.value.equals( ( (SegmentLevel) o ).value );
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }
}
