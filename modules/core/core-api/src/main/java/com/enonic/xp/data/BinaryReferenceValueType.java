package com.enonic.xp.data;

import com.enonic.xp.util.BinaryReference;

final class BinaryReferenceValueType
    extends ValueType<BinaryReference>
{
    BinaryReferenceValueType()
    {
        super( "BinaryReference", JavaTypeConverters.BINARY_REFERENCE );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newBinaryReference( convertNullSafe( object ) );
    }
}
