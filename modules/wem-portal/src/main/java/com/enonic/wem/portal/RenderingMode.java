package com.enonic.wem.portal;


public enum RenderingMode
{
    EDIT( "edit" ),
    PREVIEW( "preview" ),
    LIVE( "live" );

    private final String name;

    private RenderingMode( final String name )
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public static RenderingMode from( final String value )
    {
        return from( value, null );
    }

    public static RenderingMode from( final String value, final RenderingMode defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return RenderingMode.valueOf( value.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            return defaultValue;
        }
    }
}

