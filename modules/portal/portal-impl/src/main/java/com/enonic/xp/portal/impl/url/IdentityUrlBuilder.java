package com.enonic.xp.portal.impl.url;

import java.util.function.Function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.IdentityUrlParams;

final class IdentityUrlBuilder
    extends GenericEndpointUrlBuilder<IdentityUrlParams>
{
    private final Function<String, String> checksumGenerator;

    IdentityUrlBuilder( Function<String, String> checksumGenerator )
    {
        super( "idprovider" );
        this.checksumGenerator = checksumGenerator;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        boolean isSlashAPI = portalRequest.getRawPath().startsWith( "/api/" ) || portalRequest.getRawPath().startsWith( "/admin/api/" );
        if ( isSlashAPI )
        {
            url.setLength( 0 );
        }
        else
        {
            super.buildUrl( url, params );
        }

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

            params.put( "_ticket", checksumGenerator.apply( redirectionUrl ) );
        }
    }

    @Override
    protected String getBaseUrl()
    {
        return UrlContextHelper.getIdProviderServiceBaseUrl();
    }

    @Override
    protected String getTargetUriPrefix()
    {
        return "/api/idprovider";
    }

}
