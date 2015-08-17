package com.enonic.xp.data;

final class StringValue
    extends Value
{
    public StringValue( final String value )
    {
        super( ValueTypes.STRING, value );
    }

    public StringValue( final StringValue source )
    {
        super( ValueTypes.STRING, source.getObject() );
    }

    @Override
    public Value copy( final PropertyTree tree )
    {
        return new StringValue( this );
    }
}
