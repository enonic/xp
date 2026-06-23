package com.enonic.xp.security.token;

/**
 * State of a device authorization request when polled (RFC 8628 section 3.5).
 */
public enum DeviceAuthorizationState
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
