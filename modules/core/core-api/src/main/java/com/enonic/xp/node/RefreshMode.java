package com.enonic.xp.node;

public enum RefreshMode
{
    SEARCH( "search" ), VERSION( "version" ), BRANCH( "branch" ), COMMIT( "commit" ), ALL( "all" );

    private final String name;

    RefreshMode( final String name )
    {
        this.name = name;
    }

    public static RefreshMode from( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return RefreshMode.valueOf( value.toUpperCase() );
    }

    @Override
    public String toString()
    {
        return name;
    }
}
