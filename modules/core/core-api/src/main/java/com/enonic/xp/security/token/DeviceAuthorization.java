package com.enonic.xp.security.token;

/**
 * The result of starting a device authorization request (RFC 8628 section 3.2).
 */
public final class DeviceAuthorization
{
    private final String deviceCode;

    private final String userCode;

    private final long expiresInSeconds;

    private final long intervalSeconds;

    public DeviceAuthorization( final String deviceCode, final String userCode, final long expiresInSeconds, final long intervalSeconds )
    {
        this.deviceCode = deviceCode;
        this.userCode = userCode;
        this.expiresInSeconds = expiresInSeconds;
        this.intervalSeconds = intervalSeconds;
    }

    public String getDeviceCode()
    {
        return deviceCode;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public long getExpiresInSeconds()
    {
        return expiresInSeconds;
    }

    public long getIntervalSeconds()
    {
        return intervalSeconds;
    }
}
