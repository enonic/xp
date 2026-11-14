package com.enonic.xp.web.vhost;

import java.util.Set;

import org.jspecify.annotations.NullMarked;

/**
 * The authentication flows an id provider may serve on a virtual host, configured per id provider
 * in the vhost mapping, for example {@code idProvider.<name>.flows = login,autologin}. Without a
 * flow list the id provider serves whatever flows it supports, except {@link #TOKEN_FLOWS}, which a
 * configured mapping serves only when its list names them. The default virtual host, used where no
 * mapping is configured, serves every flow.
 * <p>
 * {@link #LOGIN}, {@link #AUTOLOGIN}, {@link #LOGOUT}, {@link #DEVICE}, {@link #CODE}, {@link #REGISTER} and {@link #CONSENT}
 * are XP's.
 * Any other name is a flow of the id provider app itself.
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

    /**
     * Authorization code with PKCE (RFC 6749, RFC 7636): XP serves the id provider's authorization
     * endpoint, which shows the same consent page as device login, and accepts the authorization
     * code grant at the token endpoint. Web endpoint only.
     */
    public static final String CODE = "code";

    /**
     * Client self-registration (RFC 7591): XP serves the id provider's registration endpoint, where
     * a client the id provider has no record of registers itself. Web endpoint only.
     */
    public static final String REGISTER = "register";

    /**
     * The consent page of device login and the authorization code flow, for the other id providers on
     * the virtual host whose apps have none: XP shows them the page of the {@code system} id provider
     * when that id provider serves this flow. The page's assets are served from the {@code system} id
     * provider's endpoint, so {@code idProvider.system.flows = consent} offers the page and nothing else
     * of that id provider: no interactive login, no {@link #AUTOLOGIN}, no {@link #LOGOUT}. Web endpoint
     * only.
     */
    public static final String CONSENT = "consent";

    /**
     * The flows that issue tokens to clients, or register clients for them: off on a configured
     * mapping unless its list names them.
     */
    public static final Set<String> TOKEN_FLOWS = Set.of( DEVICE, CODE, REGISTER );

    private IdProviderFlow()
    {
    }
}
