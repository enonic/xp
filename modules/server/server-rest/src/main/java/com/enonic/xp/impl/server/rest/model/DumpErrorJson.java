package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.DumpError;

public class DumpErrorJson
{
    private final String message;

    private final String type;

    private DumpErrorJson( final String message, final String type )
    {
        this.message = message;
        this.type = type;
    }

    public static DumpErrorJson from( final DumpError dumpError )
    {
        return new DumpErrorJson( dumpError.getMessage(), dumpError.getType().name() );
    }

    @SuppressWarnings("unused")
    public String getMessage()
    {
        return message;
    }

    @SuppressWarnings("unused")
    public String getType()
    {
        return type;
    }
}
