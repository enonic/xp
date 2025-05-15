package com.enonic.xp.dump;

public final class LoadError
{
    private final String error;

    private LoadError( final String error )
    {
        this.error = error;
    }

    public static LoadError error( final String message )
    {
        return new LoadError( message );
    }

    public String getError()
    {
        return error;
    }
}
