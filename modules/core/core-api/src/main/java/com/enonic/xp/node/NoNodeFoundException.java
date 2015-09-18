package com.enonic.xp.node;


import com.google.common.annotations.Beta;

@Beta
public abstract class NoNodeFoundException
    extends RuntimeException
{
    NoNodeFoundException( final String message )
    {
        super( message );
    }
}
