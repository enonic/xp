package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.datatype.InvalidValueTypeException;

public class InvalidDataException
    extends RuntimeException
{
    public InvalidDataException( final Data data, final InvalidValueTypeException e )
    {
        super( buildMessage( data, e ), e );
    }

    private static String buildMessage( final Data data, final InvalidValueTypeException e )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data: " ).append( data );
        return s.toString();
    }
}
