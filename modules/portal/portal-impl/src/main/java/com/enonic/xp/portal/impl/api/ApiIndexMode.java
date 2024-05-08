package com.enonic.xp.portal.impl.api;

import java.util.Locale;

public enum ApiIndexMode
{
    ON, OFF, AUTO;

    public static ApiIndexMode from( final String value )
    {
        return value != null ? ApiIndexMode.valueOf( value.toUpperCase( Locale.ROOT ) ) : AUTO;
    }
}
