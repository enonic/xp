package com.enonic.xp.data;

import com.enonic.xp.util.BinaryReference;

final class BinaryReferenceValue
    extends Value
{
    BinaryReferenceValue( final BinaryReference value )
    {
        super( ValueTypes.BINARY_REFERENCE, value );
    }

    BinaryReferenceValue( final BinaryReferenceValue source )
    {
        super( ValueTypes.BINARY_REFERENCE, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new BinaryReferenceValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
