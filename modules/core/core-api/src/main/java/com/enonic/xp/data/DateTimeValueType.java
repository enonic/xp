package com.enonic.xp.data;

import java.time.Instant;

final class DateTimeValueType
    extends ValueType<Instant>
{
    DateTimeValueType()
    {
        super( "DateTime", JavaTypeConverters.DATE_TIME );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newDateTime( convertNullSafe( object ) );
    }
}
