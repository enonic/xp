package com.enonic.wem.api.query;

import com.google.common.base.Preconditions;

class And
    implements Constraint
{
    private final Constraint leftConstraint, rightConstraint;

    private And( final Builder builder )
    {
        this.leftConstraint = builder.leftConstraint;
        this.rightConstraint = builder.rightConstraint;
    }

    public static Builder and()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return leftConstraint.toString() + " AND " + rightConstraint.toString();
    }

    public static class Builder
    {
        private Constraint leftConstraint, rightConstraint;

        public Builder leftConstraint( final Constraint leftConstraint )
        {
            this.leftConstraint = leftConstraint;
            return this;
        }

        public Builder rightConstraint( final Constraint rightConstraint )
        {
            this.rightConstraint = rightConstraint;
            return this;
        }

        public And build()
        {
            Preconditions.checkNotNull( leftConstraint );
            Preconditions.checkNotNull( rightConstraint );

            return new And( this );
        }

    }

}
