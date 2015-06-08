package com.enonic.xp.form;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;

@Beta
public class InvalidDataException
    extends RuntimeException
{
    private final Property property;

    public InvalidDataException( final Property property, final Throwable e )
    {
        super( buildMessage( property, null ), e );
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

    private static String buildMessage( final Property property, final String message )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data: " ).append( property );
        if ( message != null )
        {
            s.append( ": " ).append( message );
        }
        return s.toString();
    }
}
