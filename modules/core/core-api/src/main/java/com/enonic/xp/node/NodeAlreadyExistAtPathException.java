package com.enonic.xp.node;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
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
