package com.enonic.wem.api.content;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.support.AbstractId;

public class ContentId
    extends AbstractId
{
    public ContentId( final String id )
    {
        super( id );
    }

    public static ContentId from( String s )
    {
        return new ContentId( s );
    }

    public static ContentId from( EntityId entityId )
    {
        return new ContentId( entityId.toString() );
    }
}

