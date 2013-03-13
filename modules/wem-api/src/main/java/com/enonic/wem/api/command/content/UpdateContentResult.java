package com.enonic.wem.api.command.content;

import com.enonic.wem.api.content.ContentNotFoundException;

public enum UpdateContentResult
{
    SUCCESS, NOT_FOUND;

    public static UpdateContentResult from( Exception e )
    {
        if ( e instanceof ContentNotFoundException )
        {
            return NOT_FOUND;
        }
        else
        {
            throw new IllegalArgumentException( "Unable to map exception: " + e.getClass().getName() );
        }
    }
}
