package com.enonic.wem.core.entity;


import com.enonic.wem.repo.NodePath;

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
