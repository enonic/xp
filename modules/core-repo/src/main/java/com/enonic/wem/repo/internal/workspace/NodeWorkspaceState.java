package com.enonic.wem.repo.internal.workspace;

public enum NodeWorkspaceState
{
    LIVE( "live" ),
    DELETED( "deleted" );

    private final String value;

    NodeWorkspaceState( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static NodeWorkspaceState from( final String state )
    {
        if ( DELETED.value().equals( state ) )
        {
            return DELETED;
        }
        else
        {
            return LIVE;
        }
    }

}
