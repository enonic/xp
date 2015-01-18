package com.enonic.wem.api.content.page;

import java.text.MessageFormat;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.api.content.ContentId;

public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
