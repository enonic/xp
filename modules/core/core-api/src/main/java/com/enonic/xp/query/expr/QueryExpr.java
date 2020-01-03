package com.enonic.xp.query.expr;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class QueryExpr
    implements Expression
{
    private final ConstraintExpr constraint;

    private final ImmutableList<OrderExpr> orderList;

    public QueryExpr( final ConstraintExpr constraint, final Iterable<OrderExpr> orderSet )
    {
        this.constraint = constraint;

        if ( orderSet != null )
        {
            this.orderList = ImmutableList.copyOf( orderSet );
        }
        else
        {
            this.orderList = ImmutableList.of();
        }
    }

    public QueryExpr( final Iterable<OrderExpr> orderSet )
    {
        this.constraint = null;

        if ( orderSet != null )
        {
            this.orderList = ImmutableList.copyOf( orderSet );
        }
        else
        {
            this.orderList = ImmutableList.of();
        }
    }


    public static QueryExpr from( final ConstraintExpr constraint, final OrderExpr... orderSet )
    {
        return new QueryExpr( constraint, Arrays.asList( orderSet ) );
    }

    public static QueryExpr from( final ConstraintExpr constraint, final Iterable<OrderExpr> orderSet )
    {
        return new QueryExpr( constraint, orderSet );
    }

    public ConstraintExpr getConstraint()
    {
        return this.constraint;
    }

    public List<OrderExpr> getOrderList()
    {
        return this.orderList;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();

        if ( this.constraint != null )
        {
            str.append( this.constraint.toString() );
        }

        if ( !this.orderList.isEmpty() )
        {
            if ( str.length() > 0 )
            {
                str.append( " " );
            }

            str.append( "ORDER BY " );
            str.append( this.orderList.stream().map( Object::toString ).collect( Collectors.joining( ", " ) ) );
        }

        return str.toString();
    }
}
