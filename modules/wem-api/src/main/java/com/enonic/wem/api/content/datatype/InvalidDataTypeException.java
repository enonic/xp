package com.enonic.wem.api.content.datatype;

import com.enonic.wem.api.content.data.Data;

public class InvalidDataTypeException
    extends RuntimeException
{
    public InvalidDataTypeException( final Data data, final DataType expectedType )
    {
        super( buildMessage( data, expectedType ) );
    }

    private static String buildMessage( final Data data, final DataType expectedType )
    {
        return "Invalid data [" + data + "]. Type expected to be " + expectedType + ": " + data.getType();
    }
}
