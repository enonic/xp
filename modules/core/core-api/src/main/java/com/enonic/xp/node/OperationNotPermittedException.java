package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class OperationNotPermittedException
    extends RuntimeException
{
    public OperationNotPermittedException( final String message )
    {
        super( message );
    }
}
