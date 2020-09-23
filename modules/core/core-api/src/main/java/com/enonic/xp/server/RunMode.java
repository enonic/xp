package com.enonic.xp.server;

public enum RunMode
{
    DEV, PROD;

    public static RunMode get()
    {
        return get( System.getProperty( "xp.runMode" ) );
    }

    protected static RunMode get( final String value )
    {
        return DEV.name().equalsIgnoreCase( value ) ? DEV : PROD;
    }
}
