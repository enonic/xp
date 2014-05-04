package com.enonic.wem.api.value;

import java.time.Instant;

public final class DateTimeValue
    extends Value<Instant>
{
    public DateTimeValue( final Instant object )
    {
        super( ValueType.DATA, object );
    }

    @Override
    public String asString()
    {
        return this.object.toString();
    }
}
