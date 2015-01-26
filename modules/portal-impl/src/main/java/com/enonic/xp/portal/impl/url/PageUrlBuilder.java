package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.xp.portal.url.PageUrlParams;

final class PageUrlBuilder
    extends PortalUrlBuilder<PageUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        final ContentPath resolved = resolvePath();
        appendPart( url, resolved.toString() );
    }

    private ContentPath resolvePath()
    {
        return new ContentPathResolver().
            context( this.context ).
            contentService( this.contentService ).
            id( this.params.getId() ).
            path( this.params.getPath() ).
            resolve();
    }
}
