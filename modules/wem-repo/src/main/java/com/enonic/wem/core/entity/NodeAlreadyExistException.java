package com.enonic.wem.core.entity;


import com.enonic.wem.repo.NodePath;

public class NodeAlreadyExistException
    extends RuntimeException
{
    private final NodePath node;

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
