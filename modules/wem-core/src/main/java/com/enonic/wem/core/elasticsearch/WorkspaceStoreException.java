package com.enonic.wem.core.elasticsearch;

public class WorkspaceStoreException
    extends RuntimeException
{

    public WorkspaceStoreException( final String message )
    {
        super( message );
    }

    public WorkspaceStoreException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
