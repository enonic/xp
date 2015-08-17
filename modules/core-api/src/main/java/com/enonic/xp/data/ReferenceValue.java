package com.enonic.xp.data;

import com.enonic.xp.util.Reference;

final class ReferenceValue
    extends Value
{
    ReferenceValue( final Reference value )
    {
        super( ValueTypes.REFERENCE, value );
    }

    ReferenceValue( final ReferenceValue source )
    {
        super( ValueTypes.REFERENCE, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new ReferenceValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
