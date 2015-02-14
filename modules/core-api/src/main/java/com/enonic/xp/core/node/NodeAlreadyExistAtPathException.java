package com.enonic.xp.core.node;


public class NodeAlreadyExistAtPathException
    extends RuntimeException
{
    private final NodePath node;

    public NodeAlreadyExistAtPathException( final NodePath nodePath )
    {
        super( "Node already exist, path: " + nodePath );
        this.node = nodePath;
    }

    public NodePath getNode()
    {
        return node;
    }
}
