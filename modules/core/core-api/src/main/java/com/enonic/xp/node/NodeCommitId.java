package com.enonic.xp.node;


public final class NodeCommitId
    extends UUID
{
    public NodeCommitId()
    {
        super();
    }

    private NodeCommitId( final Object object )
    {
        super( object );
    }

    public static NodeCommitId from( final String string )
    {
        return new NodeCommitId( string );
    }

    public static NodeCommitId from( final Object object )
    {
        return new NodeCommitId( object );
    }
}
