package com.enonic.wem.api.query;

public class Or
    implements Constraint
{
    private final Constraint leftConstraint, rightConstraint;

    public Or( final Constraint leftConstraint, final Constraint rightConstraint )
    {
        this.leftConstraint = leftConstraint;
        this.rightConstraint = rightConstraint;
    }

    Constraint getLeftConstraint()
    {
        return leftConstraint;
    }

    Constraint getRightConstraint()
    {
        return rightConstraint;
    }

    @Override
    public String toString()
    {
        return leftConstraint.toString() + " OR " + rightConstraint.toString();
    }
}
