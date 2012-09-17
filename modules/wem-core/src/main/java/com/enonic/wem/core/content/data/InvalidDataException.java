package com.enonic.wem.core.content.data;


import com.enonic.wem.core.content.datatype.InvalidValueTypeException;

public class InvalidDataException
    extends RuntimeException
{
    public InvalidDataException( final Data data, final InvalidValueTypeException e )
    {
        super( buildMessage( data ), e );
    }

    public InvalidDataException( final Data data, final String message )
    {
        super( buildMessage( data, message ) );
    }

    private static String buildMessage( final Data data )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data: " ).append( data );
        return s.toString();
    }

    private static String buildMessage( final Data data, final String message )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data [" ).append( data ).append( "]: " ).append( message );
        return s.toString();
    }
}
