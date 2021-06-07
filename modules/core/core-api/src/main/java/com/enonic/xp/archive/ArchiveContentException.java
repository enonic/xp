package com.enonic.xp.archive;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentPath;

@PublicApi
public class ArchiveContentException
    extends RuntimeException
{
    ContentPath path;

    public ArchiveContentException( final String message )
    {
        super( message );
    }

    public ArchiveContentException( final String message, final ContentPath path )
    {
        this( message );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
