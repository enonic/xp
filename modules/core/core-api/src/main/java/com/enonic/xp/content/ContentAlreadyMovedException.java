package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ContentAlreadyMovedException
    extends MoveContentException
{

    public ContentAlreadyMovedException( final String message )
    {
        super( message );
    }

    public ContentAlreadyMovedException( final String message, final ContentPath path )
    {
        super( message, path );
    }
}
