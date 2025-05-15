package com.enonic.xp.data;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ValueTypeException
    extends RuntimeException
{
    public ValueTypeException( final String message )
    {
        super( message );
    }
}
