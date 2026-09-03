package com.enonic.xp.app.system.json;

import com.enonic.xp.dump.DumpError;

public record DumpErrorJson(String message)
{
    public static DumpErrorJson from( final DumpError dumpError )
    {
        return new DumpErrorJson( dumpError.getMessage() );
    }
}
