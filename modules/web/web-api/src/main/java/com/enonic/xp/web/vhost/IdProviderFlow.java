package com.enonic.xp.web.vhost;

import org.jspecify.annotations.NullMarked;

/**
 * The authentication flows an id provider may expose on a virtual host, configured per id provider
 * in the vhost mapping, e.g. {@code idProvider.<name>.flows = login,autologin}. Without an explicit
 * flow list no restriction applies and the id provider serves whatever flows it supports.
 * <p>
 * XP manages the flows with known functions and endpoints: {@link #LOGIN}, {@link #AUTOLOGIN},
 * {@link #LOGOUT} and {@link #DEVICE}. Any other name in the list is an additional flow of the id
 * provider app, which may use this configuration to enable flows of its own.
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
     * Non-interactive login from credentials in the request: XP's own access tokens as bearer
     * tokens (on the vhost's declared audience), then the id provider's {@code autoLogin}
     * function, e.g. for an upstream bearer token or basic authentication.
     */
    public static final String AUTOLOGIN = "autologin";

    /**
     * The id provider's {@code logout} function.
     */
    public static final String LOGOUT = "logout";

    /**
     * Device login (RFC 8628): XP serves the id provider's device authorization endpoint and
     * verification page, where a browserless client obtains XP-issued tokens for the id
     * provider's users, and accepts the device-code grant at the token endpoint. Web endpoint
     * only.
     */
    public static final String DEVICE = "device";

    private IdProviderFlow()
    {
    }
}
