package com.enonic.wem.api.command.content;

import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.support.illegalchange.IllegalEditException;

public class UpdateContentResult
{
    public enum Type
    {
        SUCCESS( "Success" ),
        NOT_FOUND( "Not found" ),
        ILLEGAL_EDIT( "Illegal edit" );

        private final String name;

        Type( final String name )
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public static final UpdateContentResult SUCCESS = new UpdateContentResult( Type.SUCCESS );

    private final Type type;

    private final String message;

    private UpdateContentResult( final Type type )
    {
        this.type = type;
        this.message = null;
    }

    private UpdateContentResult( final Type type, final String message )
    {
        this.type = type;
        this.message = message;
    }

    public Type getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    public String toString()
    {
        return type + ( message != null ? ": " + message : "" );
    }

    public static UpdateContentResult from( Exception e )
    {
        if ( e instanceof IllegalEditException )
        {
            return new UpdateContentResult( Type.ILLEGAL_EDIT, e.getMessage() );
        }
        else if ( e instanceof ContentNotFoundException )
        {
            return new UpdateContentResult( Type.NOT_FOUND, e.getMessage() );
        }
        else
        {
            throw new IllegalArgumentException( "Unable to map exception: " + e.getClass().getName() );
        }
    }
}
