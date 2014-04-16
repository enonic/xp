package com.enonic.wem.core.entity.dao;

public class NodeNotFoundException
    extends RuntimeException
{

    public NodeNotFoundException( final String message )
    {
        super( message );
    }
}
