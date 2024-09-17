package com.enonic.xp.core.internal;

import java.util.function.LongSupplier;

public final class MemoryLimitParser
{
    private final LongSupplier baselineSupplier;

    public MemoryLimitParser( final LongSupplier baselineSupplier )
    {
        this.baselineSupplier = baselineSupplier;
    }

    public long parse( final String value )
    {
        if ( value.endsWith( "%" ) )
        {
            return (long) Math.rint( baselineSupplier.getAsLong() / Double.parseDouble( value.substring( 0, value.length() - 1 ) ) );
        }
        else
        {
            return ByteSizeParser.parse( value );
        }
    }

    public static MemoryLimitParser maxHeap()
    {
        return new MemoryLimitParser( Runtime.getRuntime()::maxMemory );
    }
}
