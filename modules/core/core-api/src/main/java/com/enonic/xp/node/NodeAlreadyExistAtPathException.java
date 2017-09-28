package com.enonic.xp.node;


import com.google.common.annotations.Beta;

@Beta
public class NodeAlreadyExistAtPathException
    extends RuntimeException
{
    private final NodePath node;

    public NodeAlreadyExistAtPathException( final NodePath nodePath )
    {
        super( "Node already exists at " + nodePath );
        this.node = nodePath;
    }

    public NodePath getNode()
    {
        return node;
    }
}
