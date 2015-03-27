package com.enonic.xp.portal;


public enum RenderMode
{
    EDIT( "edit" ),
    PREVIEW( "preview" ),
    LIVE( "live" );

    private final String name;

    private RenderMode( final String name )
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static RenderMode from( final String value )
    {
        return from( value, null );
    }

    public static RenderMode from( final String value, final RenderMode defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return RenderMode.valueOf( value.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            return defaultValue;
        }
    }
}

