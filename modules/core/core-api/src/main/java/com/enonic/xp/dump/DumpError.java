package com.enonic.xp.dump;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DumpError
{
    private final String message;

    public DumpError( final String message )
    {
        this.message = message;
    }

    public static DumpError error( final String msg )
    {
        return new DumpError( msg );
    }

    public String getMessage()
    {
        return message;
    }
}
