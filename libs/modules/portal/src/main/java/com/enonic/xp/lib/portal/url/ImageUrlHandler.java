package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

final class ImageUrlHandler
    extends AbstractUrlHandler
{
    public ImageUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }

}
