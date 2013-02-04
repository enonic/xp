package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentPath;

public final class ContentAlreadyExistException
    extends BaseException
{
    private final ContentPath path;

    public ContentAlreadyExistException( final ContentPath path )
    {
        super( "Content at path [{0}] already exist", path.toString() );
        this.path = path;
    }

    public ContentPath getContentPath()
    {
        return path;
    }
}
