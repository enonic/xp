package com.enonic.wem.api.query;

class AndImpl
    implements And
{
    private final Constraint leftConstraint, rightConstraint;

    public AndImpl( final Constraint leftConstraint, final Constraint rightConstraint )
    {
        this.leftConstraint = leftConstraint;
        this.rightConstraint = rightConstraint;
    }

    @Override
    public Constraint getLeftConstraint()
    {
        return leftConstraint;
    }

    @Override
    public Constraint getRightConstraint()
    {
        return rightConstraint;
    }

    @Override
    public String toString()
    {
        return leftConstraint.toString() + " AND " + rightConstraint.toString();
    }
}
