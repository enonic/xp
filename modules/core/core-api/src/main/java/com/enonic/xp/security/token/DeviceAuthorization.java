package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

/**
 * The result of starting a device authorization request (RFC 8628 section 3.2). The expiry and
 * poll interval are the protocol's integer-seconds values ({@code expires_in} and {@code interval}).
 */
@NullMarked
public record DeviceAuthorization(String deviceCode, String userCode, int expiresInSeconds, int pollIntervalSeconds)
{
}
