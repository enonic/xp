package com.enonic.wem.core.content;


public class JsonParsingException
    extends RuntimeException
{
    public JsonParsingException( final String message, final Exception e )
    {
        super( message, e );
    }

    public JsonParsingException( final String message )
    {
        super( message );
    }
}
