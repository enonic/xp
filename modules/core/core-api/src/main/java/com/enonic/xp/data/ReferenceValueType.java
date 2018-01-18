package com.enonic.xp.data;

import com.enonic.xp.util.Reference;

final class ReferenceValueType
    extends ValueType<Reference>
{
    ReferenceValueType()
    {
        super( "Reference", JavaTypeConverters.REFERENCE );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newReference( convertNullSafe( object ) );
    }
}
