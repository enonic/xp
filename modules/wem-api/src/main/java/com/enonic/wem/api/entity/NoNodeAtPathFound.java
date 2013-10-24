package com.enonic.wem.api.entity;


public class NoNodeAtPathFound
    extends NoEntityFoundException
{
    private NodePath path;

    public NoNodeAtPathFound( NodePath path )
    {
        super( "No item at path [" + path + "] found" );
        this.path = path;
    }

    public NodePath getPath()
    {
        return path;
    }
}
