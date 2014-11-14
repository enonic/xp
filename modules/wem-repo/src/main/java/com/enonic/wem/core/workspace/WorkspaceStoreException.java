package com.enonic.wem.core.workspace;

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
