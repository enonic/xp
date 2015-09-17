package com.enonic.xp.data;

import com.enonic.xp.util.Link;

final class LinkValue
    extends Value
{
    LinkValue( final Link value )
    {
        super( ValueTypes.LINK, value );
    }

    LinkValue( final LinkValue source )
    {
        super( ValueTypes.LINK, source.getObject() );
    }

    @Override
    Value copy( final PropertyTree tree )
    {
        return new LinkValue( this );
    }

    @Override
    Object toJsonValue()
    {
        return asString();
    }
}
