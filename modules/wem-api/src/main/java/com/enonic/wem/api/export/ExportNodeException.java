package com.enonic.wem.api.export;

public class ExportNodeException
    extends RuntimeException
{
    public ExportNodeException( final String message )
    {
        super( message );
    }

    public ExportNodeException( final Throwable cause )
    {
        super( cause );
    }

    public ExportNodeException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
