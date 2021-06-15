package com.enonic.xp.data;

final class LongValue
    extends Value
{
    LongValue( final Long value )
    {
        super( ValueTypes.LONG, value );
    }

    LongValue( final LongValue source )
    {
        super( ValueTypes.LONG, source.getObject() );
    }

    @Override
    public Value copy( final PropertyTree tree )
    {
        return new LongValue( this );
    }
}
