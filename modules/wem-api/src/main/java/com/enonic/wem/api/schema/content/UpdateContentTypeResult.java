package com.enonic.wem.api.schema.content;

public enum UpdateContentTypeResult
{
    SUCCESS, NOT_FOUND;

    public static UpdateContentTypeResult from( Exception e )
    {
        if ( e instanceof ContentTypeNotFoundException )
        {
            return NOT_FOUND;
        }
        else
        {
            throw new IllegalArgumentException( "Unable to map exception: " + e.getClass().getName() );
        }
    }
}
