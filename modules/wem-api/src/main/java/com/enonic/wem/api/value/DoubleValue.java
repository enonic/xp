package com.enonic.wem.api.value;

public final class DoubleValue
    extends NumberValue<Double>
{
    public DoubleValue( final double object )
    {
        super( ValueType.DOUBLE, object );
    }
}
