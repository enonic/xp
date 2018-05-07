package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class MoveNodeException
    extends RuntimeException
{
    private NodePath path;

    public MoveNodeException( final String message )
    {
        super( message );
    }

    public MoveNodeException( final String message, final NodePath path )
    {
        this( message );
        this.path = path;
    }

    public NodePath getPath()
    {
        return path;
    }
}
