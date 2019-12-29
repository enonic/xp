package com.enonic.xp.export;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class ImportNodeException
    extends RuntimeException
{
    public ImportNodeException( final String message )
    {
        super( message );
    }

    public ImportNodeException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public ImportNodeException( final Throwable cause )
    {
        super( cause );
    }
}
