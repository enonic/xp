package com.enonic.wem.query.expr;

public final class LogicalExpr
    implements ConstraintExpr
{
    public enum Operator
    {
        AND,
        OR
    }

    private final ConstraintExpr left;

    private final ConstraintExpr right;

    private final Operator operator;

    private LogicalExpr( final ConstraintExpr left, final Operator operator, final ConstraintExpr right )
    {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ConstraintExpr getLeft()
    {
        return this.left;
    }

    public ConstraintExpr getRight()
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
        return "(" + this.left.toString() + " " + this.operator.toString() + " " + this.right.toString() + ")";
    }

    public static LogicalExpr and( final ConstraintExpr left, final ConstraintExpr right )
    {
        return new LogicalExpr( left, Operator.AND, right );
    }

    public static LogicalExpr or( final ConstraintExpr left, final ConstraintExpr right )
    {
        return new LogicalExpr( left, Operator.OR, right );
    }
}
