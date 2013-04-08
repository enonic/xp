package com.enonic.wem.api.content;

import java.text.MessageFormat;

import com.enonic.wem.api.exception.BaseException;

public final class ContentNotFoundException
    extends BaseException
{
    public ContentNotFoundException( final ContentPath path )
    {
        super( MessageFormat.format( "Content with path [{0}] was not found", path.toString() ) );
    }

    public ContentNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found", contentId.toString() ) );
    }

    public ContentNotFoundException( final ContentSelector selector )
    {
        super( MessageFormat.format( "Content with id [{0}] was not found", selector.toString() ) );
    }
}
