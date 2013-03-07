package com.enonic.wem.api.command.content;


import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.UnableToDeleteContentException;

public enum DeleteContentResult
{
    SUCCESS, NOT_FOUND, UNABLE_TO_DELETE;

    public static DeleteContentResult from( Exception e )
    {
        if ( e instanceof ContentNotFoundException )
        {
            return NOT_FOUND;
        }
        else if ( e instanceof UnableToDeleteContentException )
        {
            return UNABLE_TO_DELETE;
        }
        else
        {
            throw new IllegalArgumentException( "Unable to map exception: " + e.getClass().getName() );
        }
    }
}
