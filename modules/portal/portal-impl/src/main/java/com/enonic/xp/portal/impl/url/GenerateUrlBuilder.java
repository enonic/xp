package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.GenerateUrlParams;

final class GenerateUrlBuilder
    extends PortalUrlBuilder<GenerateUrlParams>
{
    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        url.setLength( 0 );
        appendPart( url, this.params.getPath() );
    }
}
