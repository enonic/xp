package com.enonic.wem.query;

public class Not
    implements Constraint
{
    private final Constraint constraint;

    public Not( final Constraint constraint )
    {
        this.constraint = constraint;
    }

    public Constraint getConstraint()
    {
        return constraint;
    }

    @Override
    public String toString()
    {
        return "NOT ( " + constraint + " )";
    }
}
