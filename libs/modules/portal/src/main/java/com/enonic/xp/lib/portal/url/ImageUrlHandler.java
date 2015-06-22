package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ImageUrlParams;

public final class ImageUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }
}
