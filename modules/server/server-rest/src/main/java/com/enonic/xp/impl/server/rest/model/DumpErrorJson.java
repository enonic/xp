package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.DumpError;

public class DumpErrorJson
{
    private final String message;

    private DumpErrorJson( final String message )
    {
        this.message = message;
    }

    public static DumpErrorJson from( final DumpError dumpError )
    {
        return new DumpErrorJson( dumpError.getMessage() );
    }

    @SuppressWarnings("unused")
    public String getMessage()
    {
        return message;
    }
}
