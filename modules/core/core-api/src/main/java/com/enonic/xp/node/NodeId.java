package com.enonic.xp.node;


public final class NodeId
    extends UUID
{
    public static final NodeId ROOT = NodeId.from( "000-000-000-000" );

    public NodeId()
    {
        super();
    }

    private NodeId( final Object value )
    {
        super( value );
    }

    public static NodeId from( final String string )
    {
        return new NodeId( string );
    }

    public static NodeId from( final Object object )
    {
        return new NodeId( object );
    }
}
