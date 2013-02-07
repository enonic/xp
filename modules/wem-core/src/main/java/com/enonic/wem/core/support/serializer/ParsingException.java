package com.enonic.wem.core.support.serializer;


public abstract class ParsingException
    extends RuntimeException
{
    protected ParsingException( final String message, final Exception e )
    {
        super( message, e );
    }

    protected ParsingException( final String message )
    {
        super( message );
    }
}
