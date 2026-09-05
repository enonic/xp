package com.enonic.xp.portal.impl.handler;

import java.net.URI;
import java.util.Locale;

import com.enonic.xp.web.WebRequest;

final class SameOriginCheck
{
    private SameOriginCheck()
    {
    }

    static boolean isSameOrigin( final String origin, final WebRequest request )
    {
        if ( origin == null )
        {
            return true;
        }

        final URI originUri;
        try
        {
            originUri = URI.create( origin );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }

        final String scheme = originUri.getScheme();
        final String host = originUri.getHost();
        if ( scheme == null || host == null || request.getScheme() == null || request.getHost() == null )
        {
            return false;
        }

        return scheme.equalsIgnoreCase( request.getScheme() ) && host.equalsIgnoreCase( request.getHost() ) &&
            effectivePort( scheme, originUri.getPort() ) == effectivePort( request.getScheme(), request.getPort() );
    }

    private static int effectivePort( final String scheme, final int port )
    {
        if ( port > 0 )
        {
            return port;
        }
        return switch ( scheme.toLowerCase( Locale.ROOT ) )
        {
            case "https", "wss" -> 443;
            case "http", "ws" -> 80;
            default -> -1;
        };
    }
}
