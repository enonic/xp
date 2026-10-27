package com.enonic.xp.portal.impl.idprovider;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.vhost.VirtualHost;

/**
 * The OAuth metadata documents for a request, from its mapping and the scopes of the id provider
 * they name. See {@link OAuthMetadata} for what the documents hold.
 */
@Component(service = OAuthMetadataService.class)
@NullMarked
public class OAuthMetadataService
{
    private final SecurityService securityService;

    @Activate
    public OAuthMetadataService( @Reference final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    /**
     * The protected resource metadata of a path under the request's mapping, or null when the
     * mapping has no id provider.
     */
    public @Nullable Map<String, Object> protectedResource( final WebRequest request, final String resourcePath )
    {
        final VirtualHost mapping = request.getVirtualHost();
        final IdProviderKey idProvider = mapping.getDefaultIdProviderKey();
        return idProvider == null
            ? null
            : OAuthMetadata.protectedResource( origin( request ), mapping, resourcePath, securityService.getScopes( idProvider ) );
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

    private static String origin( final WebRequest request )
    {
        return OAuthMetadata.origin( request.getScheme(), request.getHost(), request.getPort() );
    }
}
