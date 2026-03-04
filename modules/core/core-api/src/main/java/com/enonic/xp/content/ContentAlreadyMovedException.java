package com.enonic.xp.content;

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
