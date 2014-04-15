package com.enonic.wem.api.content.site;

import java.text.MessageFormat;

import com.enonic.wem.api.exception.NotFoundException;
import com.enonic.wem.api.content.ContentId;

public final class SiteNotFoundException
    extends NotFoundException
{
    public SiteNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] does not have site", contentId.toString() ) );
    }
}
