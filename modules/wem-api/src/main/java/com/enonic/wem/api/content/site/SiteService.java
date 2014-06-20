package com.enonic.wem.api.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.context.Context;

public interface SiteService
{
    public Content create( CreateSiteParams params, Context context );

    public Content update( UpdateSiteParams params, Context context );

    public Content delete( ContentId contentId, Context context );

    public Content getNearestSite( ContentId contentId, Context context );
}
