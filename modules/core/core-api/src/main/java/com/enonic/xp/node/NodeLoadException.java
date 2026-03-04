package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeLoadException
    extends RuntimeException
{
    public NodeLoadException( final String message )
    {
        super( message );
    }
}
