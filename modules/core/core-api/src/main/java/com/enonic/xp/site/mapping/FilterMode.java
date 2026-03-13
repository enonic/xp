package com.enonic.xp.site.mapping;

public enum FilterMode
{
    RENDER( "render" ), CHAIN( "chain" );

    private final String name;

    FilterMode( final String name )
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static FilterMode from( final String value )
    {
        return from( value, null );
    }

    public static FilterMode from( final String value, final FilterMode defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return FilterMode.valueOf( value.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            return defaultValue;
        }
    }
}
