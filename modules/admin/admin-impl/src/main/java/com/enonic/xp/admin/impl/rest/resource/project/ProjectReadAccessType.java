package com.enonic.xp.admin.impl.rest.resource.project;

public enum ProjectReadAccessType
{
    PUBLIC( "public" ), PRIVATE( "private" ), CUSTOM( "custom" );

    private final String value;

    ProjectReadAccessType( final String value )
    {
        this.value = value;
    }

    public static ProjectReadAccessType from( final String value )
    {
        if ( PUBLIC.value.equals( value ) )
        {
            return PUBLIC;
        }
        else if ( PRIVATE.value.equals( value ) )
        {
            return PRIVATE;
        }
        else if ( CUSTOM.value.equals( value ) )
        {
            return CUSTOM;
        }

        throw new IllegalArgumentException( "Cannot parse ProjectReadAccessType value: " + value );
    }

    public String getValue()
    {
        return value;
    }
}
