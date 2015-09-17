package com.enonic.xp.page;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.exception.NotFoundException;

@Beta
public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
