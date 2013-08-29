package com.enonic.wem.api.query;

class And
    implements Constraint
{
    private final Constraint leftConstraint, rightConstraint;

    public And( final Constraint leftConstraint, final Constraint rightConstraint )
    {
        this.leftConstraint = leftConstraint;
        this.rightConstraint = rightConstraint;
    }

    public Constraint getLeftConstraint()
    {
        return leftConstraint;
    }

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
