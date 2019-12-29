package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum ContentState
{
    DEFAULT, PENDING_DELETE;

    public static ContentState from( String value )
    {
        if ( value.equalsIgnoreCase( PENDING_DELETE.toString() ) )
        {
            return PENDING_DELETE;
        }
        else
        {
            return DEFAULT;
        }
    }
}
