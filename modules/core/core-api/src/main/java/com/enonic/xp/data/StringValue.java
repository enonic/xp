package com.enonic.xp.data;

final class StringValue
    extends Value
{
    StringValue( final String value )
    {
        super( ValueTypes.STRING, value );
    }

    StringValue( final StringValue source )
    {
        super( ValueTypes.STRING, source.getObject() );
    }

    @Override
    public Value copy( final PropertyTree tree )
    {
        return new StringValue( this );
    }
}
