package com.enonic.wem.api.content;

import com.enonic.wem.api.support.AbstractId;

public final class ContentId
    extends AbstractId
{
    private ContentId( final String id )
    {
        super( id );
    }

    public static ContentId from( String id )
    {
        return new ContentId( id );
    }

}

