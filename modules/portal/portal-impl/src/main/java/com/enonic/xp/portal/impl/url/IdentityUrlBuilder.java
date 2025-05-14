package com.enonic.xp.portal.impl.url;

import java.util.function.Function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ContextPathType;
import com.enonic.xp.portal.url.IdentityUrlParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.servlet.UriRewritingResult;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostHelper;

import static java.util.Objects.requireNonNullElseGet;

final class IdentityUrlBuilder
    extends PortalUrlBuilder<IdentityUrlParams>
{
    private boolean useLegacyContextPath;

    private final Function<String, String> checksumGenerator;

    IdentityUrlBuilder( Function<String, String> checksumGenerator )
    {
        this.checksumGenerator = checksumGenerator;
    }

    public void setUseLegacyContextPath( final boolean useLegacyContextPath )
    {
        this.useLegacyContextPath = useLegacyContextPath;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        final VirtualHost virtualHost = VirtualHostHelper.getVirtualHost( this.portalRequest.getRawRequest() );

        if ( useLegacyContextPath )
        {
            super.buildUrl( url, params );
            UrlBuilderHelper.appendAndEncodePathParts( url, this.portalRequest.getContentPath().toString() );
        }
        else
        {
            url.setLength( 0 );
            UrlBuilderHelper.appendSubPath( url, virtualHost.getTarget() );
        }

        UrlBuilderHelper.appendSubPath( url, "_" );
        UrlBuilderHelper.appendSubPath( url, "idprovider" );

        final IdProviderKey idProviderKey = requireNonNullElseGet( this.params.getIdProviderKey(), virtualHost::getDefaultIdProviderKey );
        UrlBuilderHelper.appendPart( url, idProviderKey.toString() );

        final String idProviderFunction = this.params.getIdProviderFunction();
        if ( idProviderFunction != null )
        {
            UrlBuilderHelper.appendPart( url, idProviderFunction );
        }

        final String redirectionUrl = this.params.getRedirectionUrl();
        if ( redirectionUrl != null )
        {
            params.put( "redirect", this.params.getRedirectionUrl() );

            params.put( "_ticket", checksumGenerator.apply( redirectionUrl ) );
        }
    }

    @Override
    protected String postUriRewriting( final UriRewritingResult uriRewritingResult )
    {
        if ( ContextPathType.RELATIVE == this.params.getContextPathType() )
        {
            return uriRewritingResult.getRewrittenUri();
        }

        return new LegacyVhostUrlPostRewriter( uriRewritingResult, this.portalRequest, "idprovider" ).rewrite();
    }
}
