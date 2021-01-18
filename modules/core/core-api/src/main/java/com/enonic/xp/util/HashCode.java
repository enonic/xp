package com.enonic.xp.util;

import com.enonic.xp.core.internal.HexCoder;

public abstract class HashCode
{
    private HashCode()
    {
    }

    public static HashCode fromLong( final long value )
    {
        return new LongHashCode( value );
    }

    private static final class LongHashCode
        extends HashCode
    {
        private final long value;

        LongHashCode( final long value )
        {
            this.value = value;
        }

        @Override
        public boolean equals( final Object o )
        {
            return ( o instanceof LongHashCode ) && value == ( (LongHashCode) o ).value;
        }

        @Override
        public int hashCode()
        {
            return Long.hashCode( value );
        }

        @Override
        public String toString()
        {
            return HexCoder.toHex( value );
        }
    }
}
