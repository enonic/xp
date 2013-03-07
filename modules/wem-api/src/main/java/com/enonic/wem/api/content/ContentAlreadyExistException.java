package com.enonic.wem.api.content;

import java.text.MessageFormat;

public final class ContentAlreadyExistException
    extends RuntimeException
{
    private final ContentPath path;

    public ContentAlreadyExistException( final ContentPath path )
    {
        super( MessageFormat.format( "Content at path [{0}] already exist", path.toString() ) );
        this.path = path;
    }

    public ContentPath getContentPath()
    {
        return path;
    }
}
