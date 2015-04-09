package com.enonic.xp.content.page;

import java.text.MessageFormat;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.content.ContentId;

@Beta
public final class PageNotFoundException
    extends NotFoundException
{
    public PageNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] is not a page", contentId.toString() ) );
    }
}
