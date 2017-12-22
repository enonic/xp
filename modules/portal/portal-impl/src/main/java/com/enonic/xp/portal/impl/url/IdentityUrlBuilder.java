package com.enonic.xp.portal.impl.url;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.hash.Hashing;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.url.IdentityUrlParams;

final class IdentityUrlBuilder
    extends GenericEndpointUrlBuilder<IdentityUrlParams>
{

    public IdentityUrlBuilder()
    {
        super( "idprovider" );
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        if ( this.params.getUserStoreKey() == null )
        {
            throw new IllegalArgumentException( "Could not find user store" );
        }
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

            final String jSessionId = getJSessionId();
            params.put( "_ticket", generateTicket( jSessionId ) );
        }
    }

    private String getJSessionId()
    {
        return ContextAccessor.current().
            getLocalScope().
            getSession().
            getKey().
            toString();
    }

    private String generateTicket( final String jSessionId )
    {
        return Hashing.sha1().
            newHasher().
            putString( jSessionId, Charsets.UTF_8 ).
            hash().
            toString();
    }
}