package com.enonic.xp.server;

import org.jspecify.annotations.NonNull;

public enum RunMode
{
    DEV, PROD;

    private static volatile RunMode runMode;

    public static @NonNull RunMode get()
    {
        if ( runMode != null )
        {
            return runMode;
        }
        final RunMode computed = DEV.name().equalsIgnoreCase( System.getProperty( "xp.runMode" ) ) ? DEV : PROD;
        runMode = computed;
        return computed;
    }

    public static boolean isDev()
    {
        return get() == DEV;
    }

    public static boolean isProd()
    {
        return get() == PROD;
    }

    static void set( final RunMode value )
    {
        runMode = value;
    }
}
