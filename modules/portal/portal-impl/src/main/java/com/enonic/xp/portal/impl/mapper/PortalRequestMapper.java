package com.enonic.xp.portal.impl.mapper;

import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.idprovider.OAuthHandler;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

public final class PortalRequestMapper
    implements MapSerializable
{
    private final PortalRequest request;

    public PortalRequestMapper( final PortalRequest request )
    {
        this.request = request;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "method", this.request.getMethod() );
        gen.value( "scheme", this.request.getScheme() );
        gen.value( "host", this.request.getHost() );
        gen.value( "port", this.request.getPort() );
        gen.value( "path", this.request.getPath() );
        gen.value( "rawPath", this.request.getRawPath() );
        gen.value( "url", this.request.getUrl() );
        gen.value( "remoteAddress", this.request.getRemoteAddress() );
        gen.value( "mode", Objects.toString( this.request.getMode(), null ) );
        gen.value( "webSocket", this.request.isWebSocket() );

        if ( this.request.isValidTicket() != null )
        {
            gen.value( "validTicket", this.request.isValidTicket() );
        }

        if ( this.request.getRepositoryId() != null )
        {
            gen.value( "repositoryId", this.request.getRepositoryId().toString() );
        }

        if ( this.request.getBranch() != null )
        {
            gen.value( "branch", this.request.getBranch().getValue() );
        }

        if ( this.request.getContentPath() != null )
        {
            gen.value( "contentPath", this.request.getContentPath().toString() );
        }

        if ( this.request.getContextPath() != null )
        {
            gen.value( "contextPath", this.request.getContextPath() );
        }

        final VirtualHostIdProvider idProviderSettings = this.request.getIdProviderSettings();
        if ( idProviderSettings != null )
        {
            gen.map( "idProviderSettings" );
            final VirtualHost virtualHost = this.request.getVirtualHost();
            final Set<String> audience = new LinkedHashSet<>( virtualHost.getAudience() );
            audience.add( virtualHost.identifier( this.request.getScheme(), this.request.getHost(), this.request.getPort() ) );
            new IdProviderSettingsMapper( idProviderSettings, audience ).serialize( gen );
            gen.end();
        }

        final AccessTokenInfo accessToken = ContextAccessor.current().getAuthInfo().getAccessToken();
        if ( accessToken != null )
        {
            gen.map( "accessToken" );
            gen.value( "issuer", accessToken.getIssuer() );
            gen.value( "clientId", accessToken.getClientId() );
            gen.value( "grantId", accessToken.getGrantId() );
            gen.value( "scope", accessToken.getScope().stream().sorted().toList() );
            gen.value( "audience", accessToken.getAudience().stream().sorted().toList() );
            gen.end();
        }

        @SuppressWarnings("unchecked") final Map<String, Object> oauthConsent = this.request.getRawRequest() == null
            ? null
            : (Map<String, Object>) this.request.getRawRequest().getAttribute( OAuthHandler.CONTEXT_ATTRIBUTE );
        if ( oauthConsent != null )
        {
            gen.value( "oauthConsent", oauthConsent );
        }

        serializeBody( gen );
        MapperHelper.serializeMultimap( "params", gen, this.request.getParams().asMap() );
        gen.value( "headers", this.request.getHeaders() );
        gen.value( "getHeader", (Function<String, String>) s -> request.getHeaders().get( s ) );
        gen.value( "cookies", this.request.getCookies() );
        gen.value( "locales", this.request.getLocales().stream().map( Locale::toLanguageTag ).collect( Collectors.toList() ) );
    }

    private void serializeBody( final MapGenerator gen )
    {
        if ( this.request.getContentType() == null )
        {
            return;
        }

        gen.value( "contentType", this.request.getContentType() );
        gen.value( "body", this.request.getBodyAsString() );
    }
}
