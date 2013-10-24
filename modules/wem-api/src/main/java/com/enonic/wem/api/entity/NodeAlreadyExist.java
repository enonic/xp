package com.enonic.wem.api.entity;


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
