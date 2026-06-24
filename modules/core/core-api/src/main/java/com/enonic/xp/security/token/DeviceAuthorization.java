package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;

/**
 * The result of starting a device authorization request (RFC 8628 section 3.2). The expiry and
 * poll interval are the protocol's integer-seconds values ({@code expires_in} and {@code interval}).
 */
@NullMarked
public final class DeviceAuthorization
{
    private final String deviceCode;

    private final String userCode;

    private final int expiresInSeconds;

    private final int pollIntervalSeconds;

    public DeviceAuthorization( final String deviceCode, final String userCode, final int expiresInSeconds, final int pollIntervalSeconds )
    {
        this.deviceCode = deviceCode;
        this.userCode = userCode;
        this.expiresInSeconds = expiresInSeconds;
        this.pollIntervalSeconds = pollIntervalSeconds;
    }

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public int getExpiresInSeconds()
    {
        return expiresInSeconds;
    }

    public int getPollIntervalSeconds()
    {
        return pollIntervalSeconds;
    }
}
