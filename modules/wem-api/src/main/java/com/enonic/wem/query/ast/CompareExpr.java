package com.enonic.wem.query.ast;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public final class CompareExpr
    implements ConstraintExpr
{
    public enum Operator
    {
        EQ( "=", false ),
        NEQ( "!=", false ),
        GT( ">", false ),
        GTE( ">=", false ),
        LT( "<", false ),
        LTE( "<=", false ),
        LIKE( "LIKE", false ),
        NOT_LIKE( "NOT LIKE", false ),
        IN( "IN", true ),
        NOT_IN( "NOT IN", true );

        private final String value;

        private final boolean multiple;

        private Operator( final String value, final boolean multiple )
        {
            this.value = value;
            this.multiple = multiple;
        }

        public String getValue()
        {
            return this.value;
        }

        public boolean allowMultipleValues()
        {
            return this.multiple;
        }

        public Operator negate()
        {
            switch ( this )
            {
                case EQ:
                    return NEQ;
                case NEQ:
                    return EQ;
                case GT:
                    return LTE;
                case GTE:
                    return LT;
                case LT:
                    return GTE;
                case LTE:
                    return GT;
                case LIKE:
                    return NOT_LIKE;
                case NOT_LIKE:
                    return LIKE;
                case IN:
                    return NOT_IN;
                case NOT_IN:
                    return IN;
            }

            return this;
        }
    }

    private final FieldExpr field;

    private final Operator operator;

    private final ImmutableList<ValueExpr> values;

    private CompareExpr( final FieldExpr field, final Operator operator, final ValueExpr value )
    {
        this.field = field;
        this.operator = operator;
        this.values = ImmutableList.of( value );
    }

    private CompareExpr( final FieldExpr field, final Operator operator, final Iterable<ValueExpr> values )
    {
        this.field = field;
        this.operator = operator;
        this.values = ImmutableList.copyOf( values );
    }

    public FieldExpr getField()
    {
        return this.field;
    }

    public Operator getOperator()
    {
        return this.operator;
    }

    public ValueExpr getFirstValue()
    {
        return this.values.isEmpty() ? null : this.values.get( 0 );
    }

    public List<ValueExpr> getValues()
    {
        return this.values;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder( this.field.toString() );
        str.append( " " ).append( this.operator.getValue() ).append( " " );

        if ( this.operator.allowMultipleValues() )
        {
            str.append( "(" ).append( Joiner.on( "," ).join( this.values ) ).append( ")" );
        }
        else
        {
            str.append( getFirstValue().toString() );
        }

        return str.toString();
    }

    public static CompareExpr eq( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.EQ, value );
    }

    public static CompareExpr neq( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.NEQ, value );
    }

    public static CompareExpr gt( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.GT, value );
    }

    public static CompareExpr gte( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.GTE, value );
    }

    public static CompareExpr lt( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.LT, value );
    }

    public static CompareExpr lte( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.LTE, value );
    }

    public static CompareExpr like( final FieldExpr field, final ValueExpr value )
    {
        return create( field, Operator.LIKE, value );
    }

    public static CompareExpr in( final FieldExpr field, final List<ValueExpr> values )
    {
        return create( field, Operator.IN, values );
    }

    public static CompareExpr create( final FieldExpr field, final Operator operator, final ValueExpr value )
    {
        return new CompareExpr( field, operator, value );
    }

    public static CompareExpr create( final FieldExpr field, final Operator operator, final Iterable<ValueExpr> values )
    {
        return new CompareExpr( field, operator, values );
    }
}
