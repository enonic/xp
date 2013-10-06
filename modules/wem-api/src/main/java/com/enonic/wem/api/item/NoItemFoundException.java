package com.enonic.wem.api.item;


public abstract class NoItemFoundException
    extends RuntimeException
{
    NoItemFoundException( final String message )
    {
        super( message );
    }
}
