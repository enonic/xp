package com.enonic.xp.web.vhost;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The well-known documents XP serves at the root of a host (RFC 8615), for OAuth: the protected
 * resource metadata (RFC 9728) and the authorization server metadata (RFC 8414). Both are found
 * by inserting the well-known segment between the host and the path of the thing described, so
 * {@code https://host/.well-known/oauth-protected-resource/api/mcp} describes
 * {@code https://host/api/mcp}.
 */
@NullMarked
public final class WellKnownPaths
{
    /**
     * The segment of the protected resource metadata (RFC 9728).
     */
    public static final String PROTECTED_RESOURCE = "/.well-known/oauth-protected-resource";

    /**
     * The segment of the authorization server metadata (RFC 8414).
     */
    public static final String AUTHORIZATION_SERVER = "/.well-known/oauth-authorization-server";

    private WellKnownPaths()
    {
    }

    /**
     * The path of the thing a well-known request describes, {@code /} for the root, or null when
     * the path is not a well-known document XP serves.
     */
    public static @Nullable String resourcePath( final @Nullable String path )
    {
        if ( path == null )
        {
            return null;
        }
        for ( final String prefix : new String[]{PROTECTED_RESOURCE, AUTHORIZATION_SERVER} )
        {
            if ( path.equals( prefix ) )
            {
                return "/";
            }
            if ( path.startsWith( prefix + "/" ) )
            {
                return path.substring( prefix.length() );
            }
        }
        return null;
    }

    /**
     * Which document the path asks for: {@link #PROTECTED_RESOURCE} or {@link #AUTHORIZATION_SERVER},
     * or null for neither.
     */
    public static @Nullable String documentOf( final @Nullable String path )
    {
        if ( path == null )
        {
            return null;
        }
        for ( final String prefix : new String[]{PROTECTED_RESOURCE, AUTHORIZATION_SERVER} )
        {
            if ( path.equals( prefix ) || path.startsWith( prefix + "/" ) )
            {
                return prefix;
            }
        }
        return null;
    }
}
