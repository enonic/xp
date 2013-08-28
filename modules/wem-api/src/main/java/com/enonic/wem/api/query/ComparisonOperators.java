package com.enonic.wem.api.query;

public enum ComparisonOperators
{

    EQUAL_TO( "=" ),
    NOT_EQUAL_TO( "!=" ),
    LESS_THAN( "<" ),
    LESS_THAN_OR_EQUAL_TO( "<=" ),
    GREATER_THAN( ">" ),
    GREATER_THAN_OR_EQUAL_TO( ">=" ),
    LIKE( "like" ),
    CONTAINS( "contains" );

    private String value;

    private ComparisonOperators( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
