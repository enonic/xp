package com.enonic.xp.archive;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentPath;

@PublicApi
public class RestoreContentException
    extends RuntimeException
{
    ContentPath path;

    public RestoreContentException( final String message )
    {
        super( message );
    }

    public RestoreContentException( final String message, final ContentPath path )
    {
        this( message );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
