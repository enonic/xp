package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeBinaryReferenceException
    extends RuntimeException
{
    public NodeBinaryReferenceException( final String message )
    {
        super( message );
    }
}
