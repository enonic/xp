package com.enonic.xp.portal.impl.handler.mapping;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.Site;

final class ContentResolverResult
{
    final Content content;
    final Site nearestSite;
    final String siteRelativePath;

    ContentResolverResult( final Content content, final Site nearestSite, final String siteRelativePath )
    {
        this.content = content;
        this.nearestSite = nearestSite;
        this.siteRelativePath = siteRelativePath;
    }
}
