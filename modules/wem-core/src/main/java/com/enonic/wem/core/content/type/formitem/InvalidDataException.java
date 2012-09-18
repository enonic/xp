package com.enonic.wem.core.content.type.formitem;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.datatype.InvalidValueTypeException;

public class InvalidDataException
    extends RuntimeException
{
    private Data data;

    public InvalidDataException( final Data data, final InvalidValueTypeException e )
    {
        super( buildMessage( data ), e );
        this.data = data;
    }

    public InvalidDataException( final Data data, final BreaksRegexValidationException e )
    {
        super( buildMessage( data ), e );
        this.data = data;
    }

    public InvalidDataException( final Data data, final InvalidValueException e )
    {
        super( buildMessage( data ), e );
        this.data = data;
    }

    public InvalidDataException( final Data data, final String message )
    {
        super( buildMessage( data, message ) );
        this.data = data;
    }

    public Data getData()
    {
        return data;
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
