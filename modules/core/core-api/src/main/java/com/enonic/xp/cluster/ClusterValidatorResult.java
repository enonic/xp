package com.enonic.xp.cluster;

import java.util.List;

import com.google.common.collect.Lists;

public class ClusterValidatorResult
{
    private final boolean ok;

    private final List<ClusterValidationError> errors;

    private ClusterValidatorResult( final Builder builder )
    {
        ok = builder.ok;
        errors = builder.errors;
    }

    public boolean isOk()
    {
        return ok;
    }

    public List<ClusterValidationError> getErrors()
    {
        return errors;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static ClusterValidatorResult ok()
    {
        return create().ok( true ).build();
    }

    public static final class Builder
    {
        private boolean ok = true;

        private List<ClusterValidationError> errors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder ok( final boolean val )
        {
            ok = val;
            return this;
        }

        public Builder errors( final List<ClusterValidationError> val )
        {
            this.ok = false;
            this.errors = val;
            return this;
        }

        public Builder error( final ClusterValidationError val )
        {
            this.ok = false;
            this.errors.add( val );
            return this;
        }


        public ClusterValidatorResult build()
        {
            return new ClusterValidatorResult( this );
        }
    }
}
