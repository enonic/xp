package com.enonic.wem.repo;


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
