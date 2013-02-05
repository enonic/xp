package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public final class ContentNotFoundException
    extends BaseException
{
    public ContentNotFoundException( final ContentPath path )
    {
        super( "Content with path [{0}] was not found", path.toString() );
    }

    public ContentNotFoundException( final ContentId contentId )
    {
        super( "Content with id [{0}] was not found", contentId.toString() );
    }
}
