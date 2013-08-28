package com.enonic.wem.api.query;

public class QueryObjectModelFactory
{
    public And and( final Constraint constraint1, final Constraint constraint2 )
    {
        return new AndImpl( constraint1, constraint2 );
    }

    public Comparison comparison( final Field leftOperand, final Literal rightOperand, final Operator operator )
    {
        return new ComparisonImpl( leftOperand, rightOperand, operator );
    }

    public Comparison like( final String fieldName, final String value )
    {
        return new LikeComparisonImpl( new FieldImpl( fieldName ), value );
    }


}
