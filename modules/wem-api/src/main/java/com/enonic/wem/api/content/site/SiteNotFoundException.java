package com.enonic.wem.api.content.site;

import java.text.MessageFormat;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.exception.BaseException;

public final class SiteNotFoundException
    extends BaseException
{
    public SiteNotFoundException( final ContentId contentId )
    {
        super( MessageFormat.format( "Content with id [{0}] does not have site", contentId.toString() ) );
    }
}
