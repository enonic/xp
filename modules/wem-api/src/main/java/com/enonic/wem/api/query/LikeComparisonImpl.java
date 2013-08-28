package com.enonic.wem.api.query;

class LikeComparisonImpl
    implements LikeComparison
{
    private final Field field;

    private final Literal literal;

    public LikeComparisonImpl( final Field field, final String literal )
    {
        this.field = field;
        this.literal = new LiteralImpl( literal );
    }

    @Override
    public Field getLeftOperand()
    {
        return field;
    }

    @Override
    public Literal getRightOperand()
    {
        return literal;
    }

    @Override
    public ComparisonOperators getOperator()
    {
        return ComparisonOperators.LIKE;
    }

    @Override
    public String toString()
    {
        return this.field + " " + ComparisonOperators.LIKE + " ('" + literal + "')";
    }
}
