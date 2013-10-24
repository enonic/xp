package com.enonic.wem.api.item;


public class NoNodeAtPathFound
    extends NoItemFoundException
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
