package com.enonic.wem.query.expr;

import com.google.common.base.Joiner;

import com.enonic.wem.query.Constraint;

public final class ComparisonExpr
    implements Constraint
{
    public enum Operator
    {
        EQ( "=", false ),
        NEQ( "!=", false ),
        GT( ">", false ),
        GTE( "=>", false ),
        LT( "<", false ),
        LTE( "<=", false ),
        LIKE( "LIKE", false ),
        NOT_LIKE( "NOT LIKE", false ),
        IN( "IN", true ),
        NOT_IN( "NOT IN", true );

        private final String value;

        private final boolean array;

        private Operator( final String value, final boolean array )
        {
            this.value = value;
            this.array = array;
        }

        public boolean isArrayOperator()
        {
            return this.array;
        }

        @Override
        public String toString()
        {
            return this.value;
        }
    }

    private final FieldExpr field;

    private final ValueExpr[] values;

    private final Operator operator;

    private ComparisonExpr( final FieldExpr field, final Operator operator, final ValueExpr... values )
    {
        this.field = field;
        this.values = values;
        this.operator = operator;
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    public ValueExpr getValue()
    {
        return this.values[0];
    }

    public ValueExpr[] getValues()
    {
        return this.values;
    }

    public Operator getOperator()
    {
        return this.operator;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();
        str.append( this.field.toString() ).append( " " ).append( this.operator.toString() );
        str.append( " " );

        if ( this.operator.isArrayOperator() )
        {
            str.append( "(" ).append( Joiner.on( "," ).join( this.values ) ).append( ")" );
        }
        else
        {
            str.append( this.values[0].toString() );
        }

        return str.toString();
    }

    public static ComparisonExpr eq( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.EQ, value );
    }

    public static ComparisonExpr neq( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.NEQ, value );
    }

    public static ComparisonExpr gt( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.GT, value );
    }

    public static ComparisonExpr gte( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.GTE, value );
    }

    public static ComparisonExpr lt( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.LT, value );
    }

    public static ComparisonExpr lte( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.LTE, value );
    }

    public static ComparisonExpr like( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.LIKE, value );
    }

    public static ComparisonExpr notLike( final FieldExpr field, final ValueExpr value )
    {
        return new ComparisonExpr( field, Operator.NOT_LIKE, value );
    }

    public static ComparisonExpr in( final FieldExpr field, final ValueExpr... values )
    {
        return new ComparisonExpr( field, Operator.IN, values );
    }

    public static ComparisonExpr notIn( final FieldExpr field, final ValueExpr... values )
    {
        return new ComparisonExpr( field, Operator.NOT_IN, values );
    }

}
