package com.enonic.xp.web.vhost;

import org.jspecify.annotations.NullMarked;

/**
 * The authentication flows an id provider may serve on a virtual host, configured per id provider
 * in the vhost mapping, for example {@code idProvider.<name>.flows = login,autologin}. Without a
 * flow list the id provider serves whatever flows it supports.
 * <p>
 * {@link #LOGIN}, {@link #AUTOLOGIN}, {@link #LOGOUT} and {@link #DEVICE} are XP's. Any other name
 * is a flow of the id provider app itself.
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
     * Non-interactive login from credentials in the request: XP's own access tokens as bearer tokens,
     * then the id provider's {@code autoLogin} function, for an upstream bearer token or basic
     * authentication.
     */
    public static final String AUTOLOGIN = "autologin";

    /**
     * The id provider's {@code logout} function.
     */
    public static final String LOGOUT = "logout";

    /**
     * Device login (RFC 8628): XP serves the id provider's device authorization endpoint and
     * verification page, and accepts the device-code grant at the token endpoint. Web endpoint only.
     */
    public static final String DEVICE = "device";

    private IdProviderFlow()
    {
    }
}
