package com.enonic.wem.api.node;


public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
