package com.enonic.xp.lib.portal.current;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.lib.mapper.ComponentMapper;
import com.enonic.xp.lib.mapper.ContentMapper;
import com.enonic.xp.lib.mapper.SiteMapper;

public final class PortalServiceWrapper
{
    private ContentService contentService;

    public ContentMapper currentContent()
    {
        return new GetCurrentContentHandler().execute();
    }

    public ComponentMapper currentComponent()
    {
        return new GetCurrentComponentHandler().execute();
    }

    public SiteMapper currentSite()
    {
        return new GetCurrentSiteHandler( this.contentService ).execute();
    }

    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
