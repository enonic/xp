package com.enonic.wem.core.support.serializer;


public abstract class SerializingException
    extends RuntimeException
{
    protected SerializingException( final String message, final Exception e )
    {
        super( message, e );
    }

    protected SerializingException( final String message )
    {
        super( message );
    }
}
