package com.enonic.xp.security.token;

import java.util.Set;

import org.jspecify.annotations.NullMarked;


/**
 * The OAuth grant types XP knows, as a client names them in {@code grant_types} and a token
 * request in {@code grant_type}.
 */
@NullMarked
public final class GrantTypes
{
    /**
     * Device authorization (RFC 8628).
     */
    public static final String DEVICE_CODE = "urn:ietf:params:oauth:grant-type:device_code";

    /**
     * Authorization code (RFC 6749 section 4.1).
     */
    public static final String AUTHORIZATION_CODE = "authorization_code";

    /**
     * Refresh token (RFC 6749 section 6).
     */
    public static final String REFRESH_TOKEN = "refresh_token";

    private static final Set<String> ALL = Set.of( DEVICE_CODE, AUTHORIZATION_CODE, REFRESH_TOKEN );

    private GrantTypes()
    {
    }

    /**
     * Whether XP knows the grant type.
     */
    public static boolean isKnown( final String grantType )
    {
        return ALL.contains( grantType );
    }
}
