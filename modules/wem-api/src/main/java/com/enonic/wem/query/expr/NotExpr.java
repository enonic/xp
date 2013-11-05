package com.enonic.wem.query.expr;

import com.enonic.wem.query.Constraint;

public final class NotExpr
    implements Constraint
{
    private final Constraint constraint;

    public NotExpr( final Constraint constraint )
    {
        this.constraint = constraint;
    }

    public Constraint getConstraint()
    {
        return this.constraint;
    }

    @Override
    public String toString()
    {
        return "NOT (" + this.constraint.toString() + ")";
    }

}
