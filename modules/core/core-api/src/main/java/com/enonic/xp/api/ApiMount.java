package com.enonic.xp.api;

import java.util.Locale;

public enum ApiMount
{
    API, ALL_SITES, SITE, ALL_WEBAPPS, WEBAPP, ADMIN;

    public static ApiMount from( final String value )
    {
        return ApiMount.valueOf( value.toUpperCase( Locale.ROOT ) );
    }
}
