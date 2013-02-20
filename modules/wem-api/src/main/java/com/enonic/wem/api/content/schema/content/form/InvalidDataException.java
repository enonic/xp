package com.enonic.wem.api.content.schema.content.form;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.type.InvalidDataTypeException;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;

public class InvalidDataException
    extends RuntimeException
{
    private Data data;

    public InvalidDataException( final Data data, final InvalidDataTypeException e )
    {
        super( buildMessage( data ), e );
        this.data = data;
    }

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
