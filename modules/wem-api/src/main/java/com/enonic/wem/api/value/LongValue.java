package com.enonic.wem.api.value;

public final class LongValue
    extends NumberValue<Long>
{
    public LongValue( final long object )
    {
        super( ValueType.LONG, object );
    }
}
