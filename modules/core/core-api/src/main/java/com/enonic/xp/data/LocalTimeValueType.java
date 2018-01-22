package com.enonic.xp.data;

import java.time.LocalTime;

final class LocalTimeValueType
    extends ValueType<LocalTime>
{
    LocalTimeValueType()
    {
        super( "LocalTime", JavaTypeConverters.LOCAL_TIME );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newLocalTime( convertNullSafe( object ) );
    }
}
