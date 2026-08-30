package com.enonic.xp.web.vhost;

import org.jspecify.annotations.NullMarked;

/**
 * The authentication flows an id provider may expose on a virtual host, configured per id provider
 * in the vhost mapping value, e.g. {@code enabled=login,autologin}. Without an explicit flow list
 * no restriction applies and the id provider serves whatever flows it supports.
 * <p>
 * XP manages the flows with known functions: {@link #LOGIN}, {@link #AUTOLOGIN} and
 * {@link #LOGOUT}. Any other name in the list is informational for the id provider app, which can
 * follow or ignore it - e.g. an id provider may serve a token endpoint only where {@code device}
 * is listed.
 */
@NullMarked
public final class IdProviderFlow
{
    /**
     * Interactive login: the id provider's {@code handle401} and {@code login} functions. Web
     * endpoint only.
     */
    public static final String LOGIN = "login";

    /**
     * Non-interactive login (the id provider's {@code autoLogin} function), e.g. bearer-token or
     * basic authentication.
     */
    public static final String AUTOLOGIN = "autologin";

    /**
     * The id provider's {@code logout} function.
     */
    public static final String LOGOUT = "logout";

    private IdProviderFlow()
    {
    }
}
