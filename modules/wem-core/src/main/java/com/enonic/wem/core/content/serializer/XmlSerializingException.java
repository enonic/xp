package com.enonic.wem.core.content.serializer;


import com.enonic.wem.core.support.serializer.SerializingException;

public class XmlSerializingException
    extends SerializingException
{
    public XmlSerializingException( final String message, final Exception e )
    {
        super( message, e );
    }

    public XmlSerializingException( final String message )
    {
        super( message );
    }
}
