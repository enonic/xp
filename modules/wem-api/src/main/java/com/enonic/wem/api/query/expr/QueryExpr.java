package com.enonic.wem.api.query.expr;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

public final class QueryExpr
    implements Expression
{
    private final ConstraintExpr constraint;

    private final ImmutableSet<OrderExpr> orderSet;

    public QueryExpr( final ConstraintExpr constraint, final Iterable<OrderExpr> orderSet )
    {
        this.constraint = constraint;

        if ( orderSet != null )
        {
            this.orderSet = ImmutableSet.copyOf( orderSet );
        }
        else
        {
            this.orderSet = ImmutableSet.of();
        }
    }

    public QueryExpr( final Iterable<OrderExpr> orderSet )
    {
        this.constraint = null;

        if ( orderSet != null )
        {
            this.orderSet = ImmutableSet.copyOf( orderSet );
        }
        else
        {
            this.orderSet = ImmutableSet.of();
        }
    }


    public ConstraintExpr getConstraint()
    {
        return this.constraint;
    }

    public Set<OrderExpr> getOrderSet()
    {
        return this.orderSet;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();

        if ( this.constraint != null )
        {
            str.append( this.constraint.toString() );
        }

        if ( !this.orderSet.isEmpty() )
        {
            if ( str.length() > 0 )
            {
                str.append( " " );
            }

            str.append( "ORDER BY " );
            str.append( Joiner.on( ", " ).join( this.orderSet ) );
        }

        return str.toString();
    }
}
