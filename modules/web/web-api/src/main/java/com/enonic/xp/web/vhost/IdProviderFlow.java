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
    AUTOLOGIN,

    /**
     * The id provider's {@code logout} function.
     */
    LOGOUT,

    /**
     * The id provider's custom endpoints - the controller's HTTP-method functions (pages,
     * callbacks, token endpoints, static assets). Interactive login of a controller-based id
     * provider typically needs {@link #LOGIN} and {@link #CUSTOM} together: the login page is
     * rendered by the login flow, while its assets, form posts and callbacks are custom endpoints.
     */
    CUSTOM;

    /**
     * The flows enabled when no explicit flow list is configured.
     */
    public static final Set<IdProviderFlow> DEFAULT = Set.of( LOGIN, AUTOLOGIN, LOGOUT, CUSTOM );

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
