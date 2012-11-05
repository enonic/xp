package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentPath;

public final class PathNotFoundException
    extends BaseException
{
    public PathNotFoundException( final ContentPath path )
    {
        super( "Content path [{0}] was not found", path.toString() );
    }
}
