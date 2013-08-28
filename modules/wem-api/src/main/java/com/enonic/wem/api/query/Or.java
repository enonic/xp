package com.enonic.wem.api.query;

public interface Or
    extends Constraint
{
    Constraint getLeftConstraint();

    Constraint getRightConstraint();
}
