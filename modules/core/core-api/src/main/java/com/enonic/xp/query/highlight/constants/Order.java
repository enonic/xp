package com.enonic.xp.query.highlight.constants;

public enum Order
{
    SCORE( "score" ), NONE( "none" );

    private final String value;

    Order( final String value )
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }

    public static Order from( final String state )
    {
        if ( SCORE.value().equals( state ) )
        {
            return SCORE;
        }
        if ( NONE.value().equals( state ) )
        {
            return NONE;
        }

        return null;
    }
}
