package com.enonic.wem.api.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;

public interface SiteService
{
    public Content create( CreateSiteParams params );

    public Content update( UpdateSiteParams params );

    public Content delete( ContentId contentId );

    public Content getNearestSite( ContentId contentId );
}
