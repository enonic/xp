package com.enonic.xp.core.content;

import java.text.MessageFormat;

import com.enonic.xp.core.exception.NotFoundException;

public final class ContentAlreadyExistException
    extends NotFoundException
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
