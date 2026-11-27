package com.enonic.xp.portal.impl.idprovider;

import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.web.vhost.VirtualHostHelper;

/**
 * A bearer challenge (RFC 6750) as XP sends it, pointing the client at the protected resource
 * metadata of what it asked for (RFC 9728), so the client can find where to get a token.
 */
@NullMarked
final class BearerChallenge
{
    static final String HEADER = "WWW-Authenticate";

    private static final String RESOURCE_METADATA = "resource_metadata";

    private BearerChallenge()
    {
    }

    /**
     * Whether the header value is a bearer challenge.
     */
    static boolean isBearer( final String value )
    {
        return value != null && value.regionMatches( true, 0, "Bearer", 0, 6 ) && ( value.length() == 6 || value.charAt( 6 ) == ' ' );
    }

    /**
     * The challenge with the resource metadata URL of the request added, unless it names one
     * already or the request's host and path are unknown.
     */
    static String withResourceMetadata( final String challenge, final HttpServletRequest request )
    {
        if ( challenge.toLowerCase( Locale.ROOT ).contains( RESOURCE_METADATA + "=" ) )
        {
            return challenge;
        }
        final String path = VirtualHostHelper.getRequestedPath( request );
        if ( VirtualHostHelper.getVirtualHost( request ) == null || path == null )
        {
            return challenge;
        }
        final String origin = OAuthMetadata.origin( request.getScheme(), request.getServerName(), request.getServerPort() );
        final String url = OAuthMetadata.resourceMetadataUrl( origin, path );
        return challenge + ( challenge.trim().length() > 6 ? ", " : " " ) + RESOURCE_METADATA + "=\"" + url + "\"";
    }
}
