package com.enonic.xp.data;

import java.time.LocalDate;

final class LocalDateValue
    extends Value
{
    LocalDateValue( final LocalDate value )
    {
        super( ValueTypes.LOCAL_DATE, value );
    }

    LocalDateValue( final LocalDateValue source )
    {
        super( ValueTypes.LOCAL_DATE, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new LocalDateValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
