package com.enonic.wem.query;

public final class LogicalExpr
    implements Constraint
{
    public enum Operator
    {
        AND,
        OR
    }

    private final Constraint left;

    private final Constraint right;

    private final Operator operator;

    private LogicalExpr( final Constraint left, final Operator operator, final Constraint right )
    {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Constraint getLeft()
    {
        return this.left;
    }

    public Constraint getRight()
    {
        return this.right;
    }

    public Operator getOperator()
    {
        return this.operator;
    }

    @Override
    public String toString()
    {
        return this.left + " " + this.operator + " " + this.right;
    }

    public static LogicalExpr and( final Constraint left, final Constraint right )
    {
        return new LogicalExpr( left, Operator.AND, right );
    }

    public static LogicalExpr or( final Constraint left, final Constraint right )
    {
        return new LogicalExpr( left, Operator.OR, right );
    }
}
