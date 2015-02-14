package com.enonic.xp.core.node;

public enum NodeState
{
    DEFAULT( "default" ),
    PENDING_DELETE( "pending_delete" );

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
