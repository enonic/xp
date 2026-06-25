package com.enonic.xp.security.token;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;

/**
 * The result of polling a device authorization request. When the {@link State} is
 * {@link State#APPROVED} the approved subject and the original request details (used to mint the
 * access token) are populated; otherwise they are {@code null}.
 */
@NullMarked
public record DeviceAuthorizationPoll(State state, @Nullable PrincipalKey subject, @Nullable IdProviderKey idProvider,
                                      @Nullable String audience, @Nullable String scope, @Nullable String clientId)
{
    public DeviceAuthorizationPoll
    {
        Objects.requireNonNull( state, "state is required" );
    }

    /**
     * A poll result carrying only a state, with no approval details (for the non-approved states).
     */
    public static DeviceAuthorizationPoll of( final State state )
    {
        return new DeviceAuthorizationPoll( state, null, null, null, null, null );
    }

    /**
     * State of a device authorization request when polled (RFC 8628 section 3.5).
     */
    public enum State
    {
        /**
         * The end user has not yet completed the verification step.
         */
        PENDING,

        /**
         * The client polled faster than the allowed interval and should slow down.
         */
        SLOW_DOWN,

        /**
         * The end user denied the request.
         */
        DENIED,

        /**
         * The request is unknown or has expired.
         */
        EXPIRED,

        /**
         * The end user approved the request; a token may now be issued.
         */
        APPROVED
    }
}
