package com.enonic.wem.api.query;

public interface And
    extends Constraint
{
    Constraint getLeftConstraint();

    Constraint getRightConstraint();

}
