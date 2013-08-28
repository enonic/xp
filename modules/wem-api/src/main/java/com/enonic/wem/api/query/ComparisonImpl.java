package com.enonic.wem.api.query;

class ComparisonImpl
    implements Comparison
{

    private final Field leftOperand;

    private final Literal rightOperand;

    private final Operator operator;

    ComparisonImpl( final Field leftOperand, final Literal rightOperand, final Operator operator )
    {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    @Override
    public Field getLeftOperand()
    {
        return null;
    }

    @Override
    public Literal getRightOperand()
    {
        return null;
    }

    @Override
    public ComparisonOperators getOperator()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return leftOperand.toString() + " " + operator.toString() + " '" + rightOperand.toString() + "'";
    }
}
