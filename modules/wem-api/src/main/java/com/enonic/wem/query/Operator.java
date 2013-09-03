package com.enonic.wem.query;

public enum Operator
{
    EQUAL_TO( "=" ),
    NOT_EQUAL_TO( "!=" ),
    LESS_THAN( "<" ),
    LESS_THAN_OR_EQUAL_TO( "<=" ),
    GREATER_THAN( ">" ),
    GREATER_THAN_OR_EQUAL_TO( ">=" ),
    LIKE( "LIKE" ),
    CONTAINS( "CONTAINS" );

    private String value;

    private Operator( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
