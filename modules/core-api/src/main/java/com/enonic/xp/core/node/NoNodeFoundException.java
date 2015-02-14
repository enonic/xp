package com.enonic.xp.core.node;


public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
