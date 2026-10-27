package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.MediaType;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * Serves the OAuth well-known documents at the root of a host: the protected resource metadata
 * of any path XP serves there, and the authorization server metadata of an id provider's
 * endpoints. The vhost filter has already matched the mapping serving the described path.
 */
@Component(immediate = true, service = WebHandler.class)
@NullMarked
public class WellKnownHandler
    extends BaseWebHandler
{
    private final OAuthMetadataService metadata;

    @Activate
    public WellKnownHandler( @Reference final OAuthMetadataService metadata )
    {
        super( -100, EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );
        this.metadata = metadata;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        final Object connector = webRequest.getRawRequest() == null ? null : webRequest.getRawRequest().getAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE );
        return DispatchConstants.WEB_CONNECTOR.equals( connector ) && WellKnownPaths.documentOf( webRequest.getRawPath() ) != null;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ) );
        }
        final String document = WellKnownPaths.documentOf( webRequest.getRawPath() );
        final String resourcePath =
            WellKnownPaths.resourcePath( webRequest.getRawRequest() == null ? null : VirtualHostHelper.getRequestedPath( webRequest.getRawRequest() ) );
        if ( document == null || resourcePath == null || webRequest.getVirtualHost() == null )
        {
            throw WebException.notFound( "Not a well-known document XP serves" );
        }
        final Map<String, Object> body = WellKnownPaths.PROTECTED_RESOURCE.equals( document )
            ? metadata.protectedResource( webRequest, resourcePath )
            : metadata.authorizationServer( webRequest, resourcePath );
        if ( body == null )
        {
            throw WebException.notFound( "Nothing is served at " + resourcePath + " that a token could be issued for" );
        }
        return WebResponse.create()
            .status( HttpStatus.OK )
            .contentType( MediaType.JSON_UTF_8 )
            .header( "Cache-Control", "public, max-age=300" )
            .body( body )
            .build();
    }
}
