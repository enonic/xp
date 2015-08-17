package com.enonic.xp.data;

import java.time.Instant;

final class DateTimeValue
    extends Value
{
    DateTimeValue( final Instant value )
    {
        super( ValueTypes.DATE_TIME, value );
    }

    DateTimeValue( final DateTimeValue source )
    {
        super( ValueTypes.DATE_TIME, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new DateTimeValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
