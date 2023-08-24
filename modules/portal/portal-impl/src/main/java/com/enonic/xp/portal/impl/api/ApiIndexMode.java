package com.enonic.xp.portal.impl.api;

import java.util.Objects;

public enum ApiIndexMode
{
    ON, OFF, AUTO;

    public static ApiIndexMode from( final String value )
    {
        return ApiIndexMode.valueOf( Objects.requireNonNullElse( value, "auto" ).toUpperCase() );
    }
}
