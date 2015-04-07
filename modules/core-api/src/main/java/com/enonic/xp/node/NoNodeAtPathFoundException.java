package com.enonic.xp.node;


import com.google.common.annotations.Beta;

@Beta
public class NoNodeAtPathFoundException
    extends NoNodeFoundException
{
    private final NodePath path;

    public NoNodeAtPathFoundException( NodePath path )
    {
        super( "No item at path [" + path + "] found" );
        this.path = path;
    }

    public NodePath getPath()
    {
        return path;
    }
}
