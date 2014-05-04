package com.enonic.wem.api.value;

public final class BooleanValue
    extends Value<Boolean>
{
    public BooleanValue( final boolean object )
    {
        super( ValueType.BOOLEAN, object );
    }
}
