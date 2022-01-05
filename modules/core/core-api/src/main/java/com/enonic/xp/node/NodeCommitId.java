package com.enonic.xp.node;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
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

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
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
