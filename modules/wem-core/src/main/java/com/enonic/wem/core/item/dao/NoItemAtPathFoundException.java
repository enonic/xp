package com.enonic.wem.core.item.dao;


public class NoItemAtPathFoundException
    extends NoItemFoundException
{
    private String path;

    NoItemAtPathFoundException( String path )
    {
        super( "No item at path " + path + " found" );
        this.path = path;
    }
}
