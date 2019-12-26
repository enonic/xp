package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum NodeState
{
    DEFAULT( "default" ),
    PENDING_DELETE( "pending_delete" ),
    ARCHIVED( "archived" );

    private final String value;

    NodeState( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static NodeState from( final String state )
    {
        if ( PENDING_DELETE.value().equals( state ) )
        {
            return PENDING_DELETE;
        }
        else
        {
            return DEFAULT;
        }
    }

}
