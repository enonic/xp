package com.enonic.xp.site.api;

import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum ApiContextPath
{
    DEFAULT, ANY;

    public static ApiContextPath from( final String value )
    {
        return ApiContextPath.valueOf( value.toUpperCase( Locale.ROOT ) );
    }
}
