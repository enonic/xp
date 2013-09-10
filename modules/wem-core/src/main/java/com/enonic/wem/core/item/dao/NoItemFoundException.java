package com.enonic.wem.core.item.dao;


public abstract class NoItemFoundException
    extends RuntimeException
{
    NoItemFoundException( final String message )
    {
        super( message );
    }
}
