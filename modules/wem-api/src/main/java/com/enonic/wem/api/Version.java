package com.enonic.wem.api;

import java.util.Properties;

import com.google.common.base.Strings;

public final class Version
{
    private final static String BANNER = "" +
        " _____ _____ _____ _____ _____ _____    _ _ _ _____ _____ \n" +
        "|   __|   | |     |   | |     |     |  | | | |   __|     |\n" +
        "|   __| | | |  |  | | | |-   -|   --|  | | | |   __| | | |\n" +
        "|_____|_|___|_____|_|___|_____|_____|  |_____|_____|_|_|_|\n";

    private final static Version INSTANCE = new Version();

    private String version;

    private Version()
    {
        try
        {
            loadProperties();
        }
        catch ( final Exception e )
        {
            throw new Error( "Failed to load version.properties", e );
        }
    }

    private void loadProperties()
        throws Exception
    {
        final Properties props = new Properties();
        props.load( getClass().getResourceAsStream( "version.properties" ) );

        this.version = getProperty( props, "version", "x.x.x" );

        final String timestamp = getProperty( props, "timestamp", null );
        if ( timestamp != null )
        {
            this.version = this.version.replace( "SNAPSHOT", timestamp );
        }
    }

    private static String getProperty( final Properties props, final String name, final String defValue )
    {
        final String value = props.getProperty( name );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return defValue;
        }
        else if ( value.startsWith( "${" ) )
        {
            return defValue;
        }
        else
        {
            return value;
        }
    }

    public String getName()
    {
        return "Enonic WEM";
    }

    public String getCopyright()
    {
        return "Copyright (c) 2000-2013 Enonic AS";
    }

    public String getVersion()
    {
        return this.version;
    }

    public void setVersion( final String value )
    {
        this.version = value != null ? value : "x.x.x";
    }

    public String getBanner()
    {
        return BANNER;
    }

    public String getNameAndVersion()
    {
        return getName() + " " + getVersion();
    }

    @Override
    public String toString()
    {
        return getNameAndVersion();
    }

    public static Version get()
    {
        return INSTANCE;
    }
}
