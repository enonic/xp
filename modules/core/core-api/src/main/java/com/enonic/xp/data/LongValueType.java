package com.enonic.xp.data;

final class LongValueType
    extends ValueType<Long>
{
    LongValueType()
    {
        super( "Long", JavaTypeConverters.LONG );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newLong( convertNullSafe( object ) );
    }
}
