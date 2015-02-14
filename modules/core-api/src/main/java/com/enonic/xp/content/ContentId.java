package com.enonic.xp.content;

import com.enonic.xp.support.AbstractId;
import com.enonic.xp.util.Reference;

public class ContentId
    extends AbstractId
{
    protected ContentId( final String id )
    {
        super( id );
    }

    public static ContentId from( String id )
    {
        return new ContentId( id );
    }

    public static ContentId from( final Reference reference )
    {
        return new ContentId( reference.getNodeId().toString() );
    }
}

