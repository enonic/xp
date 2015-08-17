package com.enonic.xp.data;

final class BooleanValue
    extends Value
{
    BooleanValue( final Boolean value )
    {
        super( ValueTypes.BOOLEAN, value );
    }

    BooleanValue( final BooleanValue source )
    {
        super( ValueTypes.BOOLEAN, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new BooleanValue( this );
    }
}
