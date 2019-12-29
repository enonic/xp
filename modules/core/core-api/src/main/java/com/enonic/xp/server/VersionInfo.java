package com.enonic.xp.server;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class VersionInfo
{
    private static VersionInfo INSTANCE;

    static
    {
        setDefault();
    }

    private final String version;

    private VersionInfo( final String version )
    {
        this.version = version.trim();
    }

    public String getVersion()
    {
        return this.version;
    }

    public boolean isSnapshot()
    {
        return this.version.endsWith( "SNAPSHOT" );
    }

    @Override
    public String toString()
    {
        return this.version;
    }

    public static VersionInfo get()
    {
        return INSTANCE;
    }

    public static void set( final String version )
    {
        INSTANCE = new VersionInfo( version );
    }

    public static void setDefault()
    {
        set( findVersion() );
    }

    private static String findVersion()
    {
        final String value = VersionInfo.class.getPackage().getImplementationVersion();
        return isNullOrEmpty( value ) ? "0.0.0-SNAPSHOT" : value;
    }
}
