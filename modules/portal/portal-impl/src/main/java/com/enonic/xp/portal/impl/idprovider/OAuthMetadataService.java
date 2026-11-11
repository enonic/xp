package com.enonic.xp.portal.impl.idprovider;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.IdProviderFlow;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;
import com.enonic.xp.web.vhost.VirtualHostService;

/**
 * The OAuth metadata documents for a request, from its mapping and the scopes of the id provider
 * they name. See {@link OAuthMetadata} for what the documents hold.
 */
@Component(service = OAuthMetadataService.class)
@NullMarked
public class OAuthMetadataService
{
    private final SecurityService securityService;

    private final VirtualHostService virtualHostService;

    @Activate
    public OAuthMetadataService( @Reference final SecurityService securityService,
                                 @Reference final VirtualHostService virtualHostService )
    {
        this.securityService = securityService;
        this.virtualHostService = virtualHostService;
    }

    /**
     * The protected resource metadata of a path under the request's mapping, or null when the
     * mapping has no id provider. A web mapping is its own authorization server. A mapping on another
     * connector names a web mapping that serves its default id provider's token flows, on the
     * request's scheme and port, as when both hosts sit behind the same proxy; null when there is none.
     */
    public @Nullable Map<String, Object> protectedResource( final WebRequest request, final String resourcePath )
    {
        final VirtualHost mapping = request.getVirtualHost();
        final IdProviderKey idProvider = mapping.getDefaultIdProviderKey();
        if ( idProvider == null )
        {
            return null;
        }
        if ( DispatchConstants.WEB_CONNECTOR.equals( mapping.getConnector() ) )
        {
            return OAuthMetadata.protectedResource( origin( request ), mapping, resourcePath, securityService.getScopes( idProvider ) );
        }
        final Optional<VirtualHost> web = tokenMapping( idProvider );
        if ( web.isEmpty() )
        {
            return null;
        }
        final String webOrigin = OAuthMetadata.origin( request.getScheme(), web.get().getHost(), request.getPort() );
        final String authorizationServer = OAuthMetadata.authorizationServer( webOrigin, web.get(), idProvider );
        return OAuthMetadata.protectedResource( origin( request ), resourcePath, authorizationServer, securityService.getScopes( idProvider ) );
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
            : OAuthMetadata.authorizationServer( origin( request ), mapping, idProvider, securityService.getScopes( idProvider ) );
    }

    /**
     * The authorization server metadata of an id provider enabled on the request's mapping.
     */
    public Map<String, Object> authorizationServer( final WebRequest request, final IdProviderKey idProvider )
    {
        return OAuthMetadata.authorizationServer( origin( request ), request.getVirtualHost(), idProvider, securityService.getScopes( idProvider ) );
    }

    private Optional<VirtualHost> tokenMapping( final IdProviderKey idProvider )
    {
        return virtualHostService.getVirtualHosts()
            .stream()
            .filter( mapping -> DispatchConstants.WEB_CONNECTOR.equals( mapping.getConnector() ) )
            .filter( mapping -> isSingleHost( mapping.getHost() ) )
            .filter( mapping -> servesTokenFlows( mapping.getIdProviders().get( idProvider ) ) )
            .min( Comparator.comparingInt( VirtualHost::getOrder ) );
    }

    private static boolean isSingleHost( final String host )
    {
        return !host.isEmpty() && !host.startsWith( "~" ) && !host.contains( " " );
    }

    private static boolean servesTokenFlows( final @Nullable VirtualHostIdProvider settings )
    {
        return settings != null && ( settings.serves( IdProviderFlow.DEVICE ) || settings.serves( IdProviderFlow.CODE ) );
    }

    private static String origin( final WebRequest request )
    {
        return OAuthMetadata.origin( request.getScheme(), request.getHost(), request.getPort() );
    }
}
