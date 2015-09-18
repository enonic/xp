package com.enonic.xp.query.expr;

import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

@Beta
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
            str.append( Joiner.on( ", " ).join( this.orderList ) );
        }

        return str.toString();
    }
}
