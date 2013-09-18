package com.enonic.wem.core.item.dao;


import com.enonic.wem.api.item.ItemPath;

public class NoItemAtPathFound
    extends NoItemFoundException
{
    private ItemPath path;

    NoItemAtPathFound( ItemPath path )
    {
        super( "No item at path [" + path + "] found" );
        this.path = path;
    }
}
