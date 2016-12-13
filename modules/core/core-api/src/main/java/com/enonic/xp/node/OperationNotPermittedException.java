package com.enonic.xp.node;

public class OperationNotPermittedException
    extends RuntimeException
{
    public OperationNotPermittedException( final String message )
    {
        super( message );
    }
}
