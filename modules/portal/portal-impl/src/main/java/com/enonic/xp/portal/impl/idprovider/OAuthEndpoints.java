package com.enonic.xp.portal.impl.idprovider;

import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.web.vhost.IdProviderFlow;

/**
 * The OAuth endpoints under {@code /_/idprovider/<idp>/}, and the vhost flow each needs.
 */
@NullMarked
public final class OAuthEndpoints
{
    /**
     * Device authorization (RFC 8628): a client gets a device code and a user code.
     */
    public static final String DEVICE_CODE = "device/code";

    /**
     * The consent page.
     */
    public static final String DEVICE = "device";

    /**
     * The authorization endpoint (RFC 6749): the consent page of the authorization code flow.
     */
    public static final String AUTHORIZE = "authorize";

    /**
     * The token endpoint: the device-code, authorization-code and refresh-token grants.
     */
    public static final String TOKEN = "token";

    /**
     * Dynamic client registration (RFC 7591).
     */
    public static final String REGISTER = "register";

    /**
     * The flows that issue tokens through the token endpoint. The endpoint is served while any of
     * them is enabled on the vhost.
     */
    public static final Set<String> ISSUING_FLOWS = Set.of( IdProviderFlow.DEVICE, IdProviderFlow.CODE );

    private static final Set<String> ALL = Set.of( DEVICE_CODE, DEVICE, AUTHORIZE, TOKEN, REGISTER );

    private OAuthEndpoints()
    {
    }

    /**
     * Whether the function name is a device-login endpoint.
     */
    public static boolean isEndpoint( final @Nullable String function )
    {
        return function != null && ALL.contains( function );
    }

    /**
     * The flow an endpoint needs, or null for the token endpoint, which needs any of
     * {@link #ISSUING_FLOWS}.
     */
    public static @Nullable String requiredFlow( final String endpoint )
    {
        return switch ( endpoint )
        {
            case TOKEN -> null;
            case AUTHORIZE -> IdProviderFlow.CODE;
            case REGISTER -> IdProviderFlow.REGISTER;
            default -> IdProviderFlow.DEVICE;
        };
    }
}
