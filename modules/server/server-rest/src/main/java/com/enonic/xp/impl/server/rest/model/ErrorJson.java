package com.enonic.xp.impl.server.rest.model;

public class ErrorJson
{
    private final int status;

    private final String message;

    public ErrorJson( final int status, final String message )
    {
        this.status = status;
        this.message = message;
    }

    public int getStatus()
    {
        return status;
    }

    public String getMessage()
    {
        return message;
    }
}
