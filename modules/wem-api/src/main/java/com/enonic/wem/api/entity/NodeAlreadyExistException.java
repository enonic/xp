package com.enonic.wem.api.entity;


public class NodeAlreadyExistException
    extends RuntimeException
{
    private NodePath node;

    public NodeAlreadyExistException( final NodePath node )
    {
        super( "Node already exist: " + node );
        this.node = node;
    }

    public NodePath getNode()
    {
        return node;
    }
}
