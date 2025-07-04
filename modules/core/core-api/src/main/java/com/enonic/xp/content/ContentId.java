package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.UUID;
import com.enonic.xp.util.Reference;

@PublicApi
public final class ContentId extends UUID
{
    private ContentId( final String id )
    {
        super(id);
    }

    private ContentId( final Object id )
    {
        super( id );
    }

    public static ContentId from( final String id )
    {
        return new ContentId( id );
    }

    public static ContentId from( final Object id )
    {
        return new ContentId( id );
    }

    public static ContentId from( final Reference reference )
    {
        return new ContentId( reference.getNodeId() );
    }
}
