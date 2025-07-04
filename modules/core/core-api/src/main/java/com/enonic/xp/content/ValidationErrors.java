package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public final class ValidationErrors
{
    private final ImmutableList<ValidationError> errors;

    private ValidationErrors( final Collection<ValidationError> validationErrors )
    {
        errors = ImmutableList.copyOf( validationErrors );
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Returns true if there were any errors.
     * @return true if there were any errors
     */
    public boolean hasErrors()
    {
        return !errors.isEmpty();
    }

    /**
     * Returns a stream of {@link ValidationError} instances.
     * @return a stream of {@link ValidationError} instances
     */
    public Stream<ValidationError> stream()
    {
        return errors.stream();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ValidationErrors that = (ValidationErrors) o;
        return errors.equals( that.errors );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( errors );
    }

    public static final class Builder
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
