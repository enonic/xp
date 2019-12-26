package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodeAlreadyMovedException
    extends MoveNodeException
{
    public NodeAlreadyMovedException( final String message )
    {
        super( message );
    }

    public NodeAlreadyMovedException( final String message, final NodePath path )
    {
        super( message, path );
    }
}
