package com.enonic.xp.form;


import com.enonic.xp.data.Property;

public class InvalidDataException
    extends RuntimeException
{
    private final Property property;

    public InvalidDataException( final Property property, final Throwable e )
    {
        super( buildMessage( property ), e );
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
}
