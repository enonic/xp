package com.enonic.xp.node;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
