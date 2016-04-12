package com.enonic.xp.portal.impl.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;

final class IdentityUrlBuilder
    extends PortalUrlBuilder<IdentityUrlParams>
{

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, this.portalRequest.getContentPath().toString() );
        appendPart( url, "_" );
        appendPart( url, "identity" );
        appendPart( url, this.params.getUserStoreKey().toString() );
        appendPart( url, this.params.getIdProviderFunction() );
        params.put( "redirect", this.params.getRedirectionUrl() );
    }
}
