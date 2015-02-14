package com.enonic.xp.core.content;

public enum ContentState
{
    DEFAULT, PENDING_DELETE;

    public static ContentState from( String value )
    {
        if ( value.toUpperCase().equals( PENDING_DELETE.toString() ) )
        {
            return PENDING_DELETE;
        }
        else
        {
            return DEFAULT;
        }
    }
}
