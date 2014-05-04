package com.enonic.wem.api.value;

import java.time.LocalDate;

public final class DateValue
    extends Value<LocalDate>
{
    public DateValue( final LocalDate object )
    {
        super( ValueType.DATA, object );
    }

    @Override
    public String asString()
    {
        return this.object.toString();
    }
}
