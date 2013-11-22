package com.enonic.wem.query.expr;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public final class QueryExpr
    implements Expression
{
    private final ConstraintExpr constraint;

    private final ImmutableList<OrderExpr> orderList;

    public QueryExpr( final ConstraintExpr constraint, final Iterable<OrderExpr> orderList )
    {
        this.constraint = constraint;

        if ( orderList != null )
        {
            this.orderList = ImmutableList.copyOf( orderList );
        }
        else
        {
            this.orderList = ImmutableList.of();
        }
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
