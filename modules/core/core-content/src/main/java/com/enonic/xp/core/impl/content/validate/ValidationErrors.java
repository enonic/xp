package com.enonic.xp.core.impl.content.validate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public final class ValidationErrors
    implements Iterable<ValidationError>
{
    private final ImmutableList<ValidationError> validationErrors;

    private ValidationErrors( final ImmutableList<ValidationError> validationErrors )
    {
        this.validationErrors = validationErrors;
    }

    private ValidationErrors( final Builder builder )
    {
        this.validationErrors = ImmutableList.copyOf( builder.dataValidationErrors );
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

    public int size()
    {
        return this.validationErrors.size();
    }

    public boolean hasErrors()
    {
        return !this.validationErrors.isEmpty();
    }

    public ValidationError getFirst()
    {
        return this.validationErrors.isEmpty() ? null : this.validationErrors.iterator().next();
    }

    ImmutableList<ValidationError> getValidationErrors()
    {
        return validationErrors;
    }

    @Override
    public Iterator<ValidationError> iterator()
    {
        return this.validationErrors.iterator();
    }

    @Override
    public int hashCode()
    {
        return this.validationErrors.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ValidationErrors ) && this.validationErrors.equals( ( (ValidationErrors) o ).validationErrors );
    }

    @Override
    public String toString()
    {
        return this.validationErrors.toString();
    }

    public static class Builder
    {
        private final Set<ValidationError> dataValidationErrors = new HashSet<>();

        public Builder add( final ValidationError dataValidationError )
        {
            this.dataValidationErrors.add( dataValidationError );
            return this;
        }

        public Builder addAll( final ValidationErrors validationErrors )
        {
            this.dataValidationErrors.addAll( validationErrors.getValidationErrors() );
            return this;
        }

        public ValidationErrors build()
        {
            return new ValidationErrors( this );
        }

    }

}
