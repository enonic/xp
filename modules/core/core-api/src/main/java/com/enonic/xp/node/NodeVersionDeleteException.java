package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeVersionDeleteException
    extends RuntimeException
{
    public NodeVersionDeleteException( final String message )
    {
        super( message );
    }
}
