package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.LoadError;

public class LoadErrorJson
{
    private final String message;

    private LoadErrorJson( final String message )
    {
        this.message = message;
    }

    public static LoadErrorJson from( final LoadError error )
    {
        return new LoadErrorJson( error.getError() );
    }

    @SuppressWarnings("unused")
    public String getMessage()
    {
        return message;
    }
}
