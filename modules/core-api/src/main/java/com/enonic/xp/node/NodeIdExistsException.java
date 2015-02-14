package com.enonic.xp.node;

public class NodeIdExistsException
    extends RuntimeException
{
    public NodeIdExistsException( final NodeId nodeId )
    {
        super( "Node already exist, id: " + nodeId );
    }
}
