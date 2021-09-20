package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class ValidationErrors
    extends AbstractImmutableEntityList<ValidationError>
{
    private ValidationErrors( final Collection<ValidationError> validationErrors )
    {
        super( ImmutableList.copyOf( validationErrors ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<ValidationError> validationErrors = new ArrayList<>();

        public Builder add( final ValidationError dataValidationError )
        {
            this.validationErrors.add( dataValidationError );
            return this;
        }

        public Builder addAll( final Collection<ValidationError> validationErrors )
        {
            this.validationErrors.addAll( validationErrors );
            return this;
        }

        public ValidationErrors build()
        {
            return new ValidationErrors( this.validationErrors );
        }
    }
}
