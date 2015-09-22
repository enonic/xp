package com.enonic.xp.data;

final class DoubleValue
    extends Value
{
    DoubleValue( final Double value )
    {
        super( ValueTypes.DOUBLE, value );
    }

    DoubleValue( final DoubleValue source )
    {
        super( ValueTypes.DOUBLE, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new DoubleValue( this );
    }
}
