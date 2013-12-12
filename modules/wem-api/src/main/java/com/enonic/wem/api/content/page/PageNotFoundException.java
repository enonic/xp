package com.enonic.wem.api.content.page;

import java.text.MessageFormat;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.exception.BaseException;

public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
