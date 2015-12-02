package com.enonic.xp.server;

import java.util.Properties;

public enum RunMode
{
    DEV,
    PROD;

    public static RunMode get()
    {
        return get( System.getProperties() );
    }

    protected static RunMode get( final Properties props )
    {
        try
        {
            final String value = props.getProperty( "xp.runMode", RunMode.PROD.toString() );
            return RunMode.valueOf( value.toUpperCase() );
        }
        catch ( final Exception e )
        {
            return RunMode.PROD;
        }
    }
}
