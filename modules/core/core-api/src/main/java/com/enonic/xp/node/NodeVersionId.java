package com.enonic.xp.node;


public final class NodeVersionId
    extends UUID
{
    public NodeVersionId()
    {
        super();
    }

    private NodeVersionId( final Object object )
    {
        super( object );
    }

    public static NodeVersionId from( final String string )
    {
        return new NodeVersionId( string );
    }

    public static NodeVersionId from( final Object object )
    {
        return new NodeVersionId( object );
    }
}
