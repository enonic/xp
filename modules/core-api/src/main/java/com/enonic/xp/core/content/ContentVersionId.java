package com.enonic.xp.core.content;

import com.enonic.xp.core.support.AbstractId;

public class ContentVersionId
    extends AbstractId
{

    private ContentVersionId( final String value )
    {
        super( value );
    }

    public static ContentVersionId from( final String id )
    {
        return new ContentVersionId( id );
    }

}


