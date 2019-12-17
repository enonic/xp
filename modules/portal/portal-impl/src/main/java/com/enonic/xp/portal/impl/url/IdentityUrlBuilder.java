package com.enonic.xp.portal.impl.url;

import java.nio.charset.StandardCharsets;

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

        if ( this.params.getIdProviderKey() == null )
        {
            throw new IllegalArgumentException( "Could not find id provider" );
        }
        appendPart( url, this.params.getIdProviderKey().toString() );

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
            putString( jSessionId, StandardCharsets.UTF_8 ).
            hash().
            toString();
    }
}