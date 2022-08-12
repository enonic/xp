package com.enonic.xp.lib.value;

import java.time.LocalDateTime;

public class LocalDateTimeHandler
{
    public LocalDateTime parse( String value )
    {
        return LocalDateTime.parse( value );
    }
}
