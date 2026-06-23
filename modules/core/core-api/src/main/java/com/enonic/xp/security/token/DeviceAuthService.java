package com.enonic.xp.security.token;

import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

/**
 * Manages the short-lived state of OAuth 2.0 Device Authorization Grant requests (RFC 8628).
 * <p>
 * State is held in the cluster-shared map with a TTL, so it expires automatically and no
 * cleanup is required. Issued access tokens are stateless and independent of this state.
 */
@NullMarked
public interface DeviceAuthService
{
    /**
     * Starts a device authorization request, returning the device/user codes.
     */
    DeviceAuthorization start( DeviceAuthorizationParams params );

    /**
     * Resolves a pending request to its device code by the user-entered code.
     */
    Optional<String> findByUserCode( IdProviderKey idProvider, String userCode );

    /**
     * Approves or denies a pending request, binding the authenticated subject when approved.
     *
     * @return {@code true} if a pending request was updated.
     */
    boolean resolve( IdProviderKey idProvider, String deviceCode, boolean approved, @Nullable PrincipalKey subject );

    /**
     * Polls a request, enforcing the minimum interval and consuming the request once approved
     * (the device code is single-use).
     */
    DeviceAuthorizationPoll poll( IdProviderKey idProvider, String deviceCode );
}
