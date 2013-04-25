package com.enonic.wem.api.content.schema.content.form;


import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.InvalidPropertyTypeException;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;

public class InvalidDataException
    extends RuntimeException
{
    private Property property;

    public InvalidDataException( final Property property, final InvalidPropertyTypeException e )
    {
        super( buildMessage( property ), e );
        this.property = property;
    }

    public InvalidDataException( final Property property, final InvalidValueTypeException e )
    {
        super( buildMessage( property ), e );
        this.property = property;
    }

    public InvalidDataException( final Property property, final BreaksRegexValidationException e )
    {
        super( buildMessage( property ), e );
        this.property = property;
    }

    public InvalidDataException( final Property property, final InvalidValueException e )
    {
        super( buildMessage( property ), e );
        this.property = property;
    }

    public InvalidDataException( final Property property, final String message )
    {
        super( buildMessage( property, message ) );
        this.property = property;
    }

    public Property getProperty()
    {
        return property;
    }

    private static String buildMessage( final Property property )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data: " ).append( property );
        return s.toString();
    }

    private static String buildMessage( final Property property, final String message )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data [" ).append( property ).append( "]: " ).append( message );
        return s.toString();
    }
}
