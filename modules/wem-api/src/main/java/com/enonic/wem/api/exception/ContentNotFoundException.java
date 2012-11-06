package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentPath;

public final class ContentNotFoundException
    extends BaseException
{
    public ContentNotFoundException( final ContentPath path )
    {
        super( "Content [{0}] was not found", path.toString() );
    }
}
