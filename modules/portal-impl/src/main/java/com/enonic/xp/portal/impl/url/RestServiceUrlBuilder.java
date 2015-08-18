package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.RestServiceUrlParams;

final class RestServiceUrlBuilder
    extends PortalUrlBuilder<RestServiceUrlParams>
{

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "rest/" );
        appendPart( url, this.params.getPath() );
    }
}
