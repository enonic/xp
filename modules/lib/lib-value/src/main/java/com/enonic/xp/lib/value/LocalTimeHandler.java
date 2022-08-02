package com.enonic.xp.lib.value;

import java.time.LocalTime;

public class LocalTimeHandler
{
    public LocalTime parse( String value )
    {
        return LocalTime.parse( value );
    }
}
