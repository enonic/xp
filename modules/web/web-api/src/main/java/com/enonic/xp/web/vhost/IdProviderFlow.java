package com.enonic.xp.web.vhost;

import java.util.Optional;

/**
 * The authentication flows an id provider may expose on a virtual host. Which flows are enabled is
 * configured per id provider in the vhost mapping value, e.g. {@code enabled=login,autologin}.
 */
public enum IdProviderFlow
{
    /**
     * Interactive login (the id provider's {@code handle401} function).
     */
    LOGIN,

    /**
     * Non-interactive login: session and bearer/self-issued-token acceptance
     * (the id provider's {@code autoLogin} function and the core token authenticator).
     */
    AUTOLOGIN,

    /**
     * OAuth 2.0 Device Authorization Grant issuance endpoints.
     */
    DEVICE,

    /**
     * Native-app (loopback redirect) issuance endpoint.
     */
    NATIVE;

    public static Optional<IdProviderFlow> from( final String value )
    {
        for ( final IdProviderFlow flow : values() )
        {
            if ( flow.name().equalsIgnoreCase( value ) )
            {
                return Optional.of( flow );
            }
        }
        return Optional.empty();
    }
}
