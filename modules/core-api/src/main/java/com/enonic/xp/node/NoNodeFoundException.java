package com.enonic.xp.node;


public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
