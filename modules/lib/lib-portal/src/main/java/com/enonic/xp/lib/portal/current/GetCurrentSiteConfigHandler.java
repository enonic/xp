package com.enonic.xp.lib.portal.current;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.site.Site;

public final class GetCurrentSiteConfigHandler
    extends GetCurrentAbstractHandler
{
    public PropertyTreeMapper execute()
    {
        final Site site = getSite();
        final ApplicationKey applicationKey = this.request.getApplicationKey();
        if ( site != null && applicationKey != null )
        {
            final PropertyTree siteConfigPropertyTree = site.getSiteConfig( applicationKey );
            if ( siteConfigPropertyTree != null )
            {
                return new PropertyTreeMapper( siteConfigPropertyTree );
            }
        }
        return null;
    }
}
