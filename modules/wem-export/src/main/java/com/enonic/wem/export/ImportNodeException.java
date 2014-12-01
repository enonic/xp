package com.enonic.wem.export;

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
}
