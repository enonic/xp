package com.enonic.wem.admin.rest.resource;

public class ErrorJson {

    private final String message;

    public ErrorJson( final String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
