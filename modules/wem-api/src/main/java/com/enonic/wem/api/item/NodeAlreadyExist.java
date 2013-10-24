package com.enonic.wem.api.item;


public class NodeAlreadyExist
    extends RuntimeException
{
    private NodePath node;

    public NodeAlreadyExist( final NodePath node )
    {
        super( "Node already exist: " + node );
        this.node = node;
    }

    public NodePath getNode()
    {
        return node;
    }
}
