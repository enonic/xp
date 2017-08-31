package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ContentAlreadyMovedException
    extends MoveContentException
{

    public ContentAlreadyMovedException( final String message )
    {
        super( message );
    }
}
