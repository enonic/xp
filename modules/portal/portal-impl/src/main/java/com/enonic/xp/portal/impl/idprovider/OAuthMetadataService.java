package com.enonic.xp.portal.impl.idprovider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.portal.impl.api.ScopeRegistry;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

/**
 * The OAuth metadata documents for a request, from its mapping and the scopes that let a token
 * into the APIs the mappings serve.
 * See {@link OAuthMetadata} for what the documents hold.
 */
@Component(service = OAuthMetadataService.class)
@NullMarked
public class OAuthMetadataService
{
    private final ScopeRegistry scopeService;

    private final VirtualHostService virtualHostService;

    @Activate
    public OAuthMetadataService( @Reference final ScopeRegistry scopeService, @Reference final VirtualHostService virtualHostService )
    {
        this.scopeService = scopeService;
        this.virtualHostService = virtualHostService;
    }

    /**
     * The protected resource metadata of a path under the request's mapping, or null when nothing
     * says where a token for it is issued. Every id provider enabled on the mapping adds its
     * authorization server, the default id provider first, as a client that is given no other reason
     * to choose takes the first: the one the mapping names for the id provider, or without one, on
     * a web mapping that serves the id provider's {@code device} or {@code code} flow, the mapping
     * itself. The scopes are those of the APIs the mapping serves.
     */
    public @Nullable Map<String, Object> protectedResource( final WebRequest request, final String resourcePath )
    {
        final VirtualHost mapping = request.getVirtualHost();
        final String origin = origin( request );
        final Set<String> authorizationServers = new LinkedHashSet<>();
        final Set<String> scopes = new LinkedHashSet<>();
        mapping.getIdProviders().forEach( ( idProvider, settings ) -> {
            final String authorizationServer = authorizationServer( origin, mapping, idProvider, settings );
            if ( authorizationServer != null )
            {
                authorizationServers.add( authorizationServer );
            }
        } );
        if ( !authorizationServers.isEmpty() )
        {
            scopes.addAll( scopeService.getScopes( List.of( mapping ), false ).getIds() );
        }
        return authorizationServers.isEmpty() ? null : OAuthMetadata.protectedResource( origin, resourcePath, authorizationServers, scopes );
    }

    private static @Nullable String authorizationServer( final String origin, final VirtualHost mapping, final IdProviderKey idProvider,
                                                         final VirtualHostIdProvider settings )
    {
        if ( settings.getAuthorizationServer() != null )
        {
            return settings.getAuthorizationServer();
        }
        final boolean issuesTokens = DispatchConstants.WEB_CONNECTOR.equals( mapping.getConnector() ) &&
            ( settings.serves( IdProviderFlow.DEVICE ) || settings.serves( IdProviderFlow.CODE ) );
        return issuesTokens ? OAuthMetadata.authorizationServer( origin, mapping, idProvider ) : null;
    }

    /**
     * The authorization server metadata of the id provider whose endpoints sit at the path under
     * the request's mapping, or null when no enabled id provider's endpoints sit there.
     */
    public @Nullable Map<String, Object> authorizationServer( final WebRequest request, final String externalPath )
    {
        final VirtualHost mapping = request.getVirtualHost();
        final IdProviderKey idProvider = OAuthMetadata.idProviderOf( mapping, externalPath );
        return idProvider == null
            ? null
            : OAuthMetadata.authorizationServer( origin( request ), mapping, idProvider, issuable( request, idProvider ) );
    }

    /**
     * The authorization server metadata of an id provider enabled on the request's mapping.
     */
    public Map<String, Object> authorizationServer( final WebRequest request, final IdProviderKey idProvider )
    {
        return OAuthMetadata.authorizationServer( origin( request ), request.getVirtualHost(), idProvider, issuable( request, idProvider ) );
    }

    /**
     * The scopes a token of the id provider can be used with: those of the APIs served by the
     * mappings its {@code autologin} flow is on.
     */
    private Scopes issuable( final WebRequest request, final IdProviderKey idProvider )
    {
        // The mappings of the configuration are not in use while virtual hosts are off.
        final List<VirtualHost> configured = virtualHostService.isEnabled() ? virtualHostService.getVirtualHosts() : List.of();
        final List<VirtualHost> accepting =
            Stream.concat( Stream.ofNullable( request.getVirtualHost() ), configured.stream() )
                .distinct()
                .filter( mapping -> {
                    final VirtualHostIdProvider settings = mapping.getIdProviders().get( idProvider );
                    return settings != null && settings.serves( IdProviderFlow.AUTOLOGIN );
                } )
                .toList();
        final boolean managementByDefault = IdProviderKey.system().equals( idProvider ) && configured.stream()
            .noneMatch( mapping -> DispatchConstants.MANAGEMENT_CONNECTOR.equals( mapping.getConnector() ) );
        return scopeService.getScopes( accepting, managementByDefault );
    }

    private static String origin( final WebRequest request )
    {
        return OAuthMetadata.origin( request.getScheme(), request.getHost(), request.getPort() );
    }
}
