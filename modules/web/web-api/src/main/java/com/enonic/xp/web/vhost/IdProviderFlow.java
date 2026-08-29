package com.enonic.xp.web.vhost;

import java.util.Optional;
import java.util.Set;

/**
 * The authentication flows an id provider may expose on a virtual host. Which flows are enabled is
 * configured per id provider in the vhost mapping value, e.g. {@code enabled=login,autologin}.
 */
public enum IdProviderFlow
{
    /**
     * Interactive login (the id provider's {@code handle401} function), triggered by a 401 response.
     */
    LOGIN,

    /**
     * Non-interactive login (the id provider's {@code autoLogin} function), e.g. bearer-token or
     * basic authentication.
     */
    AUTOLOGIN;

    /**
     * The flows enabled when no explicit flow list is configured.
     */
    public static final Set<IdProviderFlow> DEFAULT = Set.of( LOGIN, AUTOLOGIN );

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
