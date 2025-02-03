package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.RewritePathStrategy;
import com.enonic.xp.web.WebRequest;

public class RewritePathStrategyFactory
{

    public static RewritePathStrategy mediaRewriteStrategy( final WebRequest webRequest )
    {
        if ( webRequest instanceof PortalRequest portalRequest )
        {
            return new RequestRewritePathStrategy( portalRequest );
        }
        else
        {
            return new OfflineRewritePathStrategy();
        }
    }

}
