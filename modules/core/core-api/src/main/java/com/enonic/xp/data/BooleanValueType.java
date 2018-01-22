package com.enonic.xp.data;

final class BooleanValueType
    extends ValueType<Boolean>
{
    BooleanValueType()
    {
        super( "Boolean", JavaTypeConverters.BOOLEAN );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newBoolean( convertNullSafe( object ) );
    }
}
