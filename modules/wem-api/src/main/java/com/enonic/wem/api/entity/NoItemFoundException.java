package com.enonic.wem.api.entity;


public abstract class NoItemFoundException
    extends RuntimeException
{
    NoItemFoundException( final String message )
    {
        super( message );
    }
}
