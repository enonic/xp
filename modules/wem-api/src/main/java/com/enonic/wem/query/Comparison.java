package com.enonic.wem.query;

public class Comparison
    implements Constraint
{
    private Field leftOperand;

    private Literal rightOperand;

    private Operator operator;

    protected Comparison( final Operator operator )
    {
        this.operator = operator;
    }

    public Comparison( final Field leftOperand, final Literal rightOperand, final Operator operator )
    {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operator = operator;
    }

    @Override
    public String toString()
    {
        return this.leftOperand.toString() + " " + this.operator.getValue() + " " + this.rightOperand.toString();
    }

}
