package com.enonic.wem.core.content.type.formitem;


import com.enonic.wem.core.content.data.Data;

public class InvalidValueException
    extends Exception
{
    public InvalidValueException( Data data, final String message )
    {
        super( buildMessage( data, message ) );
    }

    private static String buildMessage( final Data data, final String message )
    {
        return "Invalid value in [" + data + "]. " + message + ": " + data.getValue();
    }
}
