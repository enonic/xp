package com.enonic.xp.cluster;

import java.util.List;

import com.google.common.collect.Lists;

public class ClusterValidatorResult
{
    private final List<ClusterValidationError> errors;
    
    private final List<ClusterValidationWarning> warnings;

    private ClusterValidatorResult( final Builder builder )
    {
        errors = builder.errors;
        warnings = builder.warnings;
    }

    public ClusterValidationStatus getStatus()
    {
        if (!errors.isEmpty()) {
            return ClusterValidationStatus.RED;
        }
        if (!warnings.isEmpty()) {
            return ClusterValidationStatus.YELLOW;
        }
        return ClusterValidationStatus.GREEN;
    }

    public List<ClusterValidationError> getErrors()
    {
        return errors;
    }

    public List<ClusterValidationWarning> getWarnings()
    {
        return warnings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static ClusterValidatorResult ok()
    {
        return create().build();
    }

    public static final class Builder
    {
        private List<ClusterValidationError> errors = Lists.newArrayList();
        
        private List<ClusterValidationWarning> warnings = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder errors( final List<ClusterValidationError> errors )
        {
            this.errors = errors;
            return this;
        }

        public Builder error( final ClusterValidationError error )
        {
            this.errors.add( error );
            return this;
        }

        public Builder warnings( final List<ClusterValidationWarning> warnings )
        {
            this.warnings = warnings;
            return this;
        }

        public Builder warning( final ClusterValidationWarning warning )
        {
            this.warnings.add( warning );
            return this;
        }

        public ClusterValidatorResult build()
        {
            return new ClusterValidatorResult( this );
        }
    }
}
