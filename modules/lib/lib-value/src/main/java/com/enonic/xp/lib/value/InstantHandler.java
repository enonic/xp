package com.enonic.xp.lib.value;

import java.time.Instant;

public class InstantHandler
{
    public Instant parse( String value )
    {
        return Instant.parse( value );
    }
}
