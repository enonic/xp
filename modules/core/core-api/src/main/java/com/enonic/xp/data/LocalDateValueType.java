package com.enonic.xp.data;

import java.time.LocalDate;

final class LocalDateValueType
    extends ValueType<LocalDate>
{
    LocalDateValueType()
    {
        super( "LocalDate", JavaTypeConverters.LOCAL_DATE );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newLocalDate( convertNullSafe( object ) );
    }
}
