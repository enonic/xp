package com.enonic.wem.core.entity;


public abstract class NoEntityFoundException
    extends RuntimeException
{
    NoEntityFoundException( final String message )
    {
        super( message );
    }
}
