package com.enonic.xp.data;

import java.time.LocalDateTime;

final class LocalDateTimeValue
    extends Value
{
    LocalDateTimeValue( final LocalDateTime value )
    {
        super( ValueTypes.LOCAL_DATE_TIME, value );
    }

    LocalDateTimeValue( final LocalDateTimeValue source )
    {
        super( ValueTypes.LOCAL_DATE_TIME, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new LocalDateTimeValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
