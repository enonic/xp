package com.enonic.xp.data;

final class DoubleValueType
    extends ValueType<Double>
{
    DoubleValueType()
    {
        super( "Double", JavaTypeConverters.DOUBLE );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newDouble( convertNullSafe( object ) );
    }
}
