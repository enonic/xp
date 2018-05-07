package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
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
