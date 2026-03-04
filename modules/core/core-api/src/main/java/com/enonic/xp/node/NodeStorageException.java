package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeStorageException extends RuntimeException
{
    public NodeStorageException( final String message )
    {
        super( message );
    }

    public NodeStorageException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
