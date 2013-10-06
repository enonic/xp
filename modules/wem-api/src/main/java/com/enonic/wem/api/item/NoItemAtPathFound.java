package com.enonic.wem.api.item;


public class NoItemAtPathFound
    extends NoItemFoundException
{
    private ItemPath path;

    NoItemAtPathFound( ItemPath path )
    {
        super( "No item at path [" + path + "] found" );
        this.path = path;
    }

    public ItemPath getPath()
    {
        return path;
    }
}
