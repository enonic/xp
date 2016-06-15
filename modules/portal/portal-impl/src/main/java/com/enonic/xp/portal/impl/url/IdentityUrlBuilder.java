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
        appendPart( url, "idprovider" );
        appendPart( url, this.params.getUserStoreKey().toString() );

        final String idProviderFunction = this.params.getIdProviderFunction();
        if ( idProviderFunction != null )
        {
            appendPart( url, idProviderFunction );
        }

        final String redirectionUrl = this.params.getRedirectionUrl();
        if ( redirectionUrl != null )
        {
            params.put( "redirect", this.params.getRedirectionUrl() );
        }
    }
}