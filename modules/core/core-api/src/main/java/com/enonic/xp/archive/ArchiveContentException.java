package com.enonic.xp.archive;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentPath;

@PublicApi
public final class ArchiveContentException
    extends RuntimeException
{
    private final ContentPath path;

    public ArchiveContentException( final String message, final ContentPath path )
    {
        super( message );
        this.path = path;
    }

    public ContentPath getPath()
    {
        return path;
    }
}
