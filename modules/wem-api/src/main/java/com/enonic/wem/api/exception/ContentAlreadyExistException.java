package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentPath;

public final class ContentAlreadyExistException
    extends BaseException
{
    public ContentAlreadyExistException( final ContentPath path )
    {
        super( "Content at path [{0}] already exist", path.toString() );
    }
}
