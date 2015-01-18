package com.enonic.wem.api.export;

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
