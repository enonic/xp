package com.enonic.xp.core.content.page;

import java.text.MessageFormat;

import com.enonic.xp.core.exception.NotFoundException;
import com.enonic.xp.core.content.ContentId;

public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
