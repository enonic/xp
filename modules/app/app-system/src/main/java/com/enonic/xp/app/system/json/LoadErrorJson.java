package com.enonic.xp.app.system.json;

import com.enonic.xp.dump.LoadError;

public record LoadErrorJson(String message)
{
    public static LoadErrorJson from( final LoadError error )
    {
        return new LoadErrorJson( error.getError() );
    }
}
