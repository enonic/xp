package com.enonic.xp.project;

public enum ProjectReadAccessType
{
    PUBLIC, PRIVATE, CUSTOM;

    public static ProjectReadAccessType from( final String value )
    {
        if ( value.equalsIgnoreCase( PUBLIC.toString() ) )
        {
            return PUBLIC;
        }
        else if ( value.equalsIgnoreCase( PRIVATE.toString() ) )
        {
            return PRIVATE;
        }
        else if ( value.equalsIgnoreCase( CUSTOM.toString() ) )
        {
            return CUSTOM;
        }

        throw new IllegalArgumentException( "Cannot parse ProjectReadAccessType value: " + value );
    }
}
