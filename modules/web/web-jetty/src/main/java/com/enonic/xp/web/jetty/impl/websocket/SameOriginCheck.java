package com.enonic.xp.web.jetty.impl.websocket;

import java.net.URI;
import java.util.Locale;

/**
 * Default same-origin check for a WebSocket upgrade. Compares the browser-sent {@code Origin}
 * header against the request's own scheme/host/port (after vhost resolution), with default-port
 * normalisation per RFC 6454 §6.1.
 */
final class SameOriginCheck
{
    private SameOriginCheck()
    {
    }

    /**
     * @param originHeader the value of the {@code Origin} request header, or {@code null} when absent.
     * @param expectedScheme the scheme of the upgrade request (typically {@code http} or {@code https}).
     * @param expectedHost the host of the upgrade request as resolved by the vhost.
     * @param expectedPort the port of the upgrade request; default ports are accepted whether explicit or omitted.
     * @return {@code true} when the origin matches the request (or is absent), {@code false} otherwise.
     */
    static boolean check( final String originHeader, final String expectedScheme, final String expectedHost, final int expectedPort )
    {
        if ( originHeader == null )
        {
            // Non-browser clients (curl, native apps, server-to-server) omit Origin. They are not the
            // CSWSH threat model — the attack relies on a victim's browser carrying their session cookie.
            return true;
        }

        if ( "null".equals( originHeader ) )
        {
            // Opaque origin (sandboxed iframe, file://, redirected request). Apps that legitimately
            // accept opaque origins must opt in via their own checkOrigin function.
            return false;
        }

        final URI origin;
        try
        {
            origin = URI.create( originHeader );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }

        final String scheme = origin.getScheme();
        final String host = origin.getHost();
        if ( scheme == null || host == null )
        {
            return false;
        }

        if ( !scheme.equalsIgnoreCase( expectedScheme ) )
        {
            return false;
        }

        if ( !host.equalsIgnoreCase( expectedHost ) )
        {
            return false;
        }

        final int originPort = origin.getPort() == -1 ? defaultPortFor( scheme ) : origin.getPort();
        final int requestPort = isDefaultPort( expectedScheme, expectedPort ) ? defaultPortFor( expectedScheme ) : expectedPort;

        return originPort == requestPort;
    }

    private static int defaultPortFor( final String scheme )
    {
        return switch ( scheme.toLowerCase( Locale.ROOT ) )
        {
            case "https", "wss" -> 443;
            case "http", "ws" -> 80;
            default -> -1;
        };
    }

    private static boolean isDefaultPort( final String scheme, final int port )
    {
        return port == defaultPortFor( scheme );
    }
}
