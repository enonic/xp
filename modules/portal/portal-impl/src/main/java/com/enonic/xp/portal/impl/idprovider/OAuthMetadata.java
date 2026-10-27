package com.enonic.xp.portal.impl.idprovider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.token.GrantTypes;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * The OAuth metadata documents for what a vhost mapping serves: the protected resource metadata
 * (RFC 9728) of a path under the mapping, and the authorization server metadata (RFC 8414) of an
 * id provider enabled on it. Every URL in them is external, as the client sees the host.
 */
@NullMarked
final class OAuthMetadata
{
    private OAuthMetadata()
    {
    }

    /**
     * The origin of a request as the client sees it: scheme, host and a port that is not the
     * scheme's default.
     */
    static String origin( final String scheme, final String host, final int port )
    {
        final boolean defaultPort = port <= 0 || ( "https".equals( scheme ) ? port == 443 : "http".equals( scheme ) && port == 80 );
        return scheme + "://" + host + ( defaultPort ? "" : ":" + port );
    }

    /**
     * The external base of the id provider endpoints of a mapping's id provider:
     * {@code <origin><source>/_/idprovider/<idp>}.
     */
    static String authorizationServer( final String origin, final VirtualHost mapping, final IdProviderKey idProvider )
    {
        return origin + ( "/".equals( mapping.getSource() ) ? "" : mapping.getSource() ) + "/_/idprovider/" + idProvider;
    }

    /**
     * The id provider an authorization server URL under the mapping names, or null when the path
     * is not {@code <source>/_/idprovider/<idp>} for an id provider enabled on the mapping.
     */
    static @Nullable IdProviderKey idProviderOf( final VirtualHost mapping, final String externalPath )
    {
        final String base = "/".equals( mapping.getSource() ) ? "" : mapping.getSource();
        final String prefix = base + "/_/idprovider/";
        if ( !externalPath.startsWith( prefix ) || externalPath.indexOf( '/', prefix.length() ) >= 0 )
        {
            return null;
        }
        final IdProviderKey idProvider;
        try
        {
            idProvider = IdProviderKey.from( externalPath.substring( prefix.length() ) );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
        return mapping.getIdProviders().containsKey( idProvider ) ? idProvider : null;
    }

    /**
     * The protected resource metadata of a resource under the mapping: its own URL as its
     * identifier, the mapping's default id provider as its authorization server, and that id
     * provider's scopes.
     *
     * @param resourcePath the resource's path as the client sent it
     * @return the document, or null when the mapping has no id provider to authorize with
     */
    static @Nullable Map<String, Object> protectedResource( final String origin, final VirtualHost mapping, final String resourcePath,
                                                            final Scopes scopes )
    {
        final IdProviderKey idProvider = mapping.getDefaultIdProviderKey();
        if ( idProvider == null )
        {
            return null;
        }
        final Map<String, Object> document = new LinkedHashMap<>();
        document.put( "resource", origin + ( "/".equals( resourcePath ) ? "" : resourcePath ) );
        document.put( "authorization_servers", List.of( authorizationServer( origin, mapping, idProvider ) ) );
        document.put( "bearer_methods_supported", List.of( "header" ) );
        if ( !scopes.isEmpty() )
        {
            document.put( "scopes_supported", scopes.stream().map( Scope::getId ).toList() );
        }
        return document;
    }

    /**
     * The authorization server metadata of an id provider on the mapping: the endpoints XP serves
     * for it there, for the flows the mapping enables.
     */
    static Map<String, Object> authorizationServer( final String origin, final VirtualHost mapping, final IdProviderKey idProvider,
                                                    final Scopes scopes )
    {
        final VirtualHostIdProvider settings = mapping.getIdProviders().get( idProvider );
        final boolean device = settings != null && settings.serves( IdProviderFlow.DEVICE );
        final boolean code = settings != null && settings.serves( IdProviderFlow.CODE );
        final boolean register = settings != null && settings.serves( IdProviderFlow.REGISTER );
        final String base = authorizationServer( origin, mapping, idProvider );

        final Map<String, Object> document = new LinkedHashMap<>();
        document.put( "issuer", base );
        final List<String> grantTypes = new ArrayList<>();
        if ( code )
        {
            document.put( "authorization_endpoint", base + "/" + OAuthEndpoints.AUTHORIZE );
            grantTypes.add( GrantTypes.AUTHORIZATION_CODE );
        }
        if ( device )
        {
            document.put( "device_authorization_endpoint", base + "/" + OAuthEndpoints.DEVICE_CODE );
            grantTypes.add( GrantTypes.DEVICE_CODE );
        }
        if ( code || device )
        {
            document.put( "token_endpoint", base + "/" + OAuthEndpoints.TOKEN );
            grantTypes.add( GrantTypes.REFRESH_TOKEN );
        }
        if ( register )
        {
            document.put( "registration_endpoint", base + "/" + OAuthEndpoints.REGISTER );
        }
        document.put( "response_types_supported", code ? List.of( "code" ) : List.of() );
        document.put( "grant_types_supported", grantTypes );
        document.put( "token_endpoint_auth_methods_supported", List.of( "none" ) );
        document.put( "code_challenge_methods_supported", List.of( "S256" ) );
        if ( !scopes.isEmpty() )
        {
            document.put( "scopes_supported", scopes.stream().map( Scope::getId ).toList() );
        }
        return document;
    }

    /**
     * The URL of the protected resource metadata of a resource: the origin, the well-known
     * segment, and the resource's path as the client sent it.
     */
    static String resourceMetadataUrl( final String origin, final String requestedPath )
    {
        return origin + WellKnownPaths.PROTECTED_RESOURCE + ( "/".equals( requestedPath ) ? "" : requestedPath );
    }
}
