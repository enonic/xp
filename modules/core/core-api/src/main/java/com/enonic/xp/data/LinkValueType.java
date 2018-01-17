package com.enonic.xp.data;

final class LinkValueType
    extends ValueType<com.enonic.xp.util.Link>
{
    LinkValueType()
    {
        super( "Link", JavaTypeConverters.LINK );
    }

    @Override
    public Value fromJsonValue( final Object object )
    {
        return ValueFactory.newLink( convert( object ) );
    }
}
