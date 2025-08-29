package com.enonic.xp.repo.impl.node;

import java.util.function.LongSupplier;

public class NodeManualOrderValueResolver
    implements LongSupplier
{
    private static final long ORDER_SPACE = Integer.MAX_VALUE;

    private static final long START_ORDER_VALUE = 0L;

    private long current = START_ORDER_VALUE;

    @Override
    public long getAsLong()
    {
        long value = current;
        current = after( value );
        return value;
    }

    public static long first()
    {
        return START_ORDER_VALUE;
    }

    public static long after( long value )
    {
        return value - ORDER_SPACE;
    }

    public static long before( final long value )
    {
        return value + ORDER_SPACE;
    }

    public static long between( final long value1, final long value2 )
    {
        return (value1 & value2) + ((value1 ^ value2) >> 1);
    }
}
