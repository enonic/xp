package com.enonic.xp.server;

import com.google.common.base.Strings;

public final class VersionInfo
{
    private final static String VERSION_PROP = "xp.version";

    private final static String BUILD_HASH_PROP = "xp.build.hash";

    private final static String BUILD_NUMBER_PROP = "xp.build.number";

    private final static String BUILD_BRANCH_PROP = "xp.build.branch";

    private final String version;

    private final String buildHash;

    private final String buildNumber;

    private final String buildBranch;

    private VersionInfo()
    {
        this.version = getProperty( VERSION_PROP, "0.0.0-SNAPSHOT" );
        this.buildHash = getProperty( BUILD_HASH_PROP, "N/A" );
        this.buildNumber = getProperty( BUILD_NUMBER_PROP, "N/A" );
        this.buildBranch = getProperty( BUILD_BRANCH_PROP, "N/A" );
    }

    public String getVersion()
    {
        return this.version;
    }

    public String getBuildHash()
    {
        return this.buildHash;
    }

    public String getBuildNumber()
    {
        return this.buildNumber;
    }

    public String getBuildBranch()
    {
        return this.buildBranch;
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
