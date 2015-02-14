package com.enonic.xp.core.convert;

import com.enonic.xp.core.content.ContentId;

final class ContentIdConverter
    implements Converter<ContentId>
{
    @Override
    public Class<ContentId> getType()
    {
        return ContentId.class;
    }

    @Override
    public ContentId convert( final Object value )
    {
        if ( value instanceof ContentId )
        {
            return (ContentId) value;
        }

        return ContentId.from( value.toString() );
    }
}
