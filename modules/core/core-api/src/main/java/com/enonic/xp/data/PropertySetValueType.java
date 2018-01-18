package com.enonic.xp.data;

final class PropertySetValueType
    extends ValueType<PropertySet>
{
    PropertySetValueType()
    {
        super( "PropertySet", JavaTypeConverters.DATA );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newPropertySet( convertNullSafe( object ) );
    }
}
