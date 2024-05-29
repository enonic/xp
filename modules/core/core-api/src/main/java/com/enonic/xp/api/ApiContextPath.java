package com.enonic.xp.api;

import java.util.Locale;

public enum ApiContextPath
{
    DEFAULT, ANY;

    public static ApiContextPath from( final String value )
    {
        return ApiContextPath.valueOf( value.toUpperCase( Locale.ROOT ) );
    }
}
