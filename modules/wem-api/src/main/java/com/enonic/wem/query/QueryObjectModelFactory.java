package com.enonic.wem.query;

public class QueryObjectModelFactory
{

    public And and( final Constraint leftConstraint, final Constraint rightConstraint )
    {
        return And.and().leftConstraint( leftConstraint ).rightConstraint( rightConstraint ).build();
    }

    public Or or( final Constraint leftConstraint, final Constraint rightConstraint )
    {
        return new Or( leftConstraint, rightConstraint );
    }

    public Not not( final Constraint constraint )
    {
        return new Not( constraint );
    }

    public Comparison equalTo( final String fieldName, final Object value )
    {
        return this.comparison( fieldName, value, Operator.EQUAL_TO );
    }

    public Comparison like( final String fieldName, final String value )
    {
        return this.comparison( fieldName, value, Operator.LIKE );
    }

    public Comparison lessThan( final String fieldName, final Object value )
    {
        return this.comparison( fieldName, value, Operator.LESS_THAN );
    }

    public Comparison notEqualTo( final String fieldName, Object value )
    {
        return this.comparison( fieldName, value, Operator.NOT_EQUAL_TO );
    }

    public Comparison lessThanOrEqualTo( final String fieldName, Object value )
    {
        return this.comparison( fieldName, value, Operator.LESS_THAN_OR_EQUAL_TO );
    }

    public Comparison greaterThan( final String fieldName, Object value )
    {
        return this.comparison( fieldName, value, Operator.GREATER_THAN );
    }

    public Comparison greaterThanOrEqualTo( final String fieldName, Object value )
    {
        return this.comparison( fieldName, value, Operator.GREATER_THAN_OR_EQUAL_TO );
    }

    public Comparison contains( final String fieldName, Object value )
    {
        return this.comparison( fieldName, value, Operator.CONTAINS );
    }

    private Comparison comparison( final String fieldName, Object value, Operator operator )
    {
        return new Comparison( this.field( fieldName ), this.literal( value ), operator );
    }

    private Field field( final String fieldName )
    {
        return new Field( fieldName );
    }

    private Literal literal( final Object value )
    {
        return new Literal( this.value( value ) );
    }

    private Value value( final Object value )
    {
        return new Value( value );
    }

}
