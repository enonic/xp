/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query.expr;

import com.enonic.wem.query.Constraint;
import com.enonic.wem.query.Expression;

/**
 * This class implements the compare expression.
 */
public abstract class BinaryExpr
    implements Constraint
{
    /**
     * Left expression.
     */
    private final Expression left;

    /**
     * Right expression.
     */
    private final Expression right;

    /**
     * Construct the operator expression.
     */
    public BinaryExpr( Expression left, Expression right )
    {
        this.left = left;
        this.right = right;
    }

    /**
     * Return the left expression.
     */
    public Expression getLeft()
    {
        return this.left;
    }

    /**
     * Return the expression.
     */
    public Expression getRight()
    {
        return this.right;
    }
}
