package com.enonic.xp.server;

import com.google.common.base.Strings;

public final class VersionInfo
{
    private final static String VERSION_PROP = "xp.version";

    private final static String BUILD_HASH_PROP = "xp.build.hash";

    private final static String BUILD_TIMESTAMP_PROP = "xp.build.timestamp";

    private final static String BUILD_NUMBER_PROP = "xp.build.number";

    private final String version;

    private final String buildHash;

    private final String buildTimestamp;

    private final String buildNumber;

    private VersionInfo()
    {
        this.version = getProperty( VERSION_PROP, "0.0.0-SNAPSHOT" );
        this.buildHash = getProperty( BUILD_HASH_PROP, "N/A" );
        this.buildTimestamp = getProperty( BUILD_TIMESTAMP_PROP, "N/A" );
        this.buildNumber = getProperty( BUILD_NUMBER_PROP, "N/A" );
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getBuildHash()
    {
        return this.buildHash;
    }

    public String getBuildTimestamp()
    {
        return this.buildTimestamp;
    }

    public String getBuildNumber()
    {
        return this.buildNumber;
    }

    public boolean isSnapshotVersion()
    {
        return this.version.endsWith( "SNAPSHOT" );
    }

    private static String getProperty( final String name, final String defValue )
    {
        final String value = System.getProperty( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value.trim();
    }

    public static VersionInfo get()
    {
        return new VersionInfo();
    }
}
