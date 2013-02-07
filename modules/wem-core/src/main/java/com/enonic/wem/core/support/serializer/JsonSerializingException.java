package com.enonic.wem.core.support.serializer;


public class JsonSerializingException
    extends SerializingException
{
    public JsonSerializingException( final String message, final Exception e )
    {
        super( message, e );
    }

    public JsonSerializingException( final String message )
    {
        super( message );
    }
}
