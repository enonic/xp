package com.enonic.xp.toolbox;

public final class ResponseException
    extends RuntimeException
{

    private final int responseCode;

    public ResponseException( final String message, final int code )
    {
        super( message );
        this.responseCode = code;
    }

    public int getResponseCode()
    {
        return responseCode;
    }
}
