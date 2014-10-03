package com.enonic.wem.api.content;

import com.enonic.wem.api.support.AbstractId;

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

}

