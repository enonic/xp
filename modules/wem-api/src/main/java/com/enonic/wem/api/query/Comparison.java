package com.enonic.wem.api.query;

public interface Comparison
    extends Constraint
{
    public Field getLeftOperand();

    public Literal getRightOperand();

    public ComparisonOperators getOperator();
}
