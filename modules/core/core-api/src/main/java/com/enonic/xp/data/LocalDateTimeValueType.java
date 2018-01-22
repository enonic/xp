package com.enonic.xp.data;

import java.time.LocalDateTime;

final class LocalDateTimeValueType
    extends ValueType<LocalDateTime>
{
    LocalDateTimeValueType()
    {
        super( "LocalDateTime", JavaTypeConverters.LOCAL_DATE_TIME );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newLocalDateTime( convertNullSafe( object ) );
    }
}
