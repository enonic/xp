package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class MoveContentException
    extends RuntimeException
{
    ContentPath path;

    public MoveContentException( final String message )
    {
        super( message );
    }

    public MoveContentException( final String message, final ContentPath path )
    {
        this( message );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
