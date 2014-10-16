package com.enonic.wem.core.entity;


public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
