package com.enonic.xp.lib.portal.current;

import com.enonic.xp.lib.content.mapper.SiteMapper;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteHandler
    extends GetCurrentAbstractHandler
{
    public SiteMapper execute()
    {
        final Site site = getSite();
        return site != null ? convert( site ) : null;
    }

    private SiteMapper convert( final Site site )
    {
        return site == null ? null : new SiteMapper( site );
    }
}
