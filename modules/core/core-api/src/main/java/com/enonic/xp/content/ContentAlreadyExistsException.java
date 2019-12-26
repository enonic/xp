package com.enonic.xp.content;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public final class ContentAlreadyExistsException
    extends NotFoundException
{
    private final ContentPath path;

    public ContentAlreadyExistsException( final ContentPath path )
    {
        super( MessageFormat.format( "Content at path [{0}] already exists", path.toString() ) );
        this.path = path;
    }

    public ContentPath getContentPath()
    {
        return path;
    }

    @Override
    public String getCode()
    {
        return "contentAlreadyExists";
    }
}
