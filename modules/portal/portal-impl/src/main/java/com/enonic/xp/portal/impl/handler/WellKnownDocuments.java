package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.net.MediaType;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.impl.idprovider.OAuthMetadataService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.vhost.VirtualHostHelper;
import com.enonic.xp.web.vhost.WellKnownPaths;

/**
 * The OAuth well-known documents of a request whose mapping the vhost filter matched by the
 * described path, on whichever connector it came.
 */
@NullMarked
final class WellKnownDocuments
{
    static final EnumSet<HttpMethod> METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private WellKnownDocuments()
    {
    }

    static WebResponse serve( final OAuthMetadataService metadata, final WebRequest webRequest )
    {
        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( METHODS );
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
