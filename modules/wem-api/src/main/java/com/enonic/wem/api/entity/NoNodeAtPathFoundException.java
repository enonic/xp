package com.enonic.wem.api.entity;


public class NoNodeAtPathFoundException
extends NoEntityFoundException
{
    private NodePath path;

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
