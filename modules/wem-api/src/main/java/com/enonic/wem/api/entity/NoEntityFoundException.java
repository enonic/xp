package com.enonic.wem.api.entity;


public abstract class NoEntityFoundException
    extends RuntimeException
{
    NoEntityFoundException( final String message )
    {
        super( message );
    }
}
