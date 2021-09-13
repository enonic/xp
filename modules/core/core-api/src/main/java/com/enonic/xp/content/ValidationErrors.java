package com.enonic.xp.content;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

public final class ValidationErrors
    extends AbstractImmutableEntitySet<ValidationError>
{
    private ValidationErrors( final Collection<ValidationError> validationErrors )
    {
        super( ImmutableSet.copyOf( validationErrors ) );
    }

    public static ValidationErrors empty()
    {
        return new ValidationErrors( ImmutableList.of() );
    }

    public static ValidationErrors from( final ValidationError... validationErrors )
    {
        return new ValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }

    public static ValidationErrors from( final Iterable<ValidationError> validationErrors )
    {
        return new ValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<ValidationError> dataValidationErrors = new LinkedHashSet<>();

        public Builder add( final ValidationError dataValidationError )
        {
            this.dataValidationErrors.add( dataValidationError );
            return this;
        }

        public Builder addAll( final Collection<ValidationError> validationErrors )
        {
            this.dataValidationErrors.addAll( validationErrors );
            return this;
        }

        public ValidationErrors build()
        {
            return new ValidationErrors( this.dataValidationErrors );
        }

    }

}
