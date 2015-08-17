package com.enonic.xp.data;

import java.time.LocalTime;

final class LocalTimeValue
    extends Value
{
    LocalTimeValue( final LocalTime value )
    {
        super( ValueTypes.LOCAL_TIME, value );
    }

    LocalTimeValue( final LocalTimeValue source )
    {
        super( ValueTypes.LOCAL_TIME, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new LocalTimeValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
