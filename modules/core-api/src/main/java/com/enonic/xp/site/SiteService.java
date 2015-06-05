package com.enonic.xp.site;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.site.CreateSiteParams;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.module.ModuleKey;

public interface SiteService
{
    SiteDescriptor getDescriptor( ModuleKey moduleKey );

    Site create( CreateSiteParams params );

    Site getNearestSite( ContentId contentId );
}
