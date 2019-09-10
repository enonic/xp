package com.enonic.xp.query.highlight.constants;

public enum TagsSchema
{
    STYLED( "styled" );

    private final String value;

    TagsSchema( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static TagsSchema from( final String state )
    {
        if ( STYLED.value().equals( state ) )
        {
            return STYLED;
        }
        return null;
    }
}
