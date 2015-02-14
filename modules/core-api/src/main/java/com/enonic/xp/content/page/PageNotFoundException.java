package com.enonic.xp.content.page;

import java.text.MessageFormat;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.content.ContentId;

public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
