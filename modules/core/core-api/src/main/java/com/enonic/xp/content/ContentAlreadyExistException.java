package com.enonic.xp.content;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
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

    public String getCode()
    {
        return "contentAlreadyExist";
    }
}
