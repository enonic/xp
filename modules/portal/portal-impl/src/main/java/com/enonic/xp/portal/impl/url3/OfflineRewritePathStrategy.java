package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.url.RewritePathStrategy;

public class OfflineRewritePathStrategy
    implements RewritePathStrategy
{
    @Override
    public String rewritePath( final String path )
    {
        return path;
    }
}
