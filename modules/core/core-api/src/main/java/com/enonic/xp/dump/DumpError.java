package com.enonic.xp.dump;

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
