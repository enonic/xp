package com.enonic.xp.archive;

import com.enonic.xp.content.ContentPath;


public final class RestoreContentException
    extends RuntimeException
{
    private final ContentPath path;

    public RestoreContentException( final String message, final ContentPath path )
    {
        super( message );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
