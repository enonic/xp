package com.enonic.xp.launcher;

import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

import static com.google.common.base.Strings.isNullOrEmpty;

public final class VersionInfo
{
    private final static VersionInfo INSTANCE = load();

    private final Properties props;

    protected VersionInfo( final Properties props )
    {
        this.props = props;
    }

    public String getVersion()
    {
        final String value = getClass().getPackage().getImplementationVersion();
        return isNullOrEmpty( value ) ? "0.0.0-SNAPSHOT" : value;
    }

    public String getBuildHash()
    {
        return getProperty( "xp.build.hash", "N/A" );
    }

    public String getBuildTimestamp()
    {
        return getProperty( "xp.build.timestamp", "N/A" );
    }

    public String getBuildBranch()
    {
        return getProperty( "xp.build.branch", "N/A" );
    }

    public Map<String, String> getAsMap()
    {
        return Maps.fromProperties( this.props );
    }

    private String getProperty( final String name, final String defValue )
    {
        final String value = this.props.getProperty( name );
        return isNullOrEmpty( value ) ? defValue : value.trim();
    }

    private static VersionInfo load()
    {
        return new VersionInfo( loadProperties() );
    }

    private static Properties loadProperties()
    {
        try
        {
            final Properties props = new Properties();
            props.load( VersionInfo.class.getResourceAsStream( "/META-INF/build.properties" ) );
            return props;
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static VersionInfo get()
    {
        return INSTANCE;
    }
}
