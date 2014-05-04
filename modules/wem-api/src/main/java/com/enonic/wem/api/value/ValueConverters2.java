package com.enonic.wem.api.value;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.converter.Converters;
import com.enonic.wem.api.converter.DefaultConverters;
import com.enonic.wem.api.entity.EntityId;

final class ValueConverters2
    extends Converters
{
    public final static ValueConverters2 INSTANCE = new ValueConverters2();

    public ValueConverters2()
    {
        // Add all default converters
        addAll( new DefaultConverters() );

        // Register string -> * converters
        register( String.class, EntityId.class, this::stringToEntityId );
        register( String.class, ContentId.class, this::stringToContentId );
    }

    private EntityId stringToEntityId( final String from )
    {
        return EntityId.from( from );
    }

    private ContentId stringToContentId( final String from )
    {
        return ContentId.from( from );
    }
}
