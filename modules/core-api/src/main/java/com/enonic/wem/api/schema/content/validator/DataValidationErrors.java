package com.enonic.wem.api.schema.content.validator;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public final class DataValidationErrors
    implements Iterable<DataValidationError>
{
    private final ImmutableList<DataValidationError> validationErrors;

    private DataValidationErrors( final ImmutableList<DataValidationError> validationErrors )
    {
        this.validationErrors = validationErrors;
    }

    public int size()
    {
        return this.validationErrors.size();
    }

    public boolean hasErrors()
    {
        return !this.validationErrors.isEmpty();
    }

    public DataValidationError getFirst()
    {
        return this.validationErrors.isEmpty() ? null : this.validationErrors.iterator().next();
    }

    private DataValidationErrors( final Builder builder )
    {
        this.validationErrors = ImmutableList.copyOf( builder.dataValidationErrors );
    }

    ImmutableList<DataValidationError> getValidationErrors()
    {
        return validationErrors;
    }

    @Override
    public Iterator<DataValidationError> iterator()
    {
        return this.validationErrors.iterator();
    }

    public int hashCode()
    {
        return this.validationErrors.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof DataValidationErrors ) && this.validationErrors.equals( ( (DataValidationErrors) o ).validationErrors );
    }


    public String toString()
    {
        return this.validationErrors.toString();
    }

    public static DataValidationErrors empty()
    {
        final ImmutableList<DataValidationError> list = ImmutableList.of();
        return new DataValidationErrors( list );
    }

    public static DataValidationErrors from( final DataValidationError... validationErrors )
    {
        return new DataValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }

    public static DataValidationErrors from( final Iterable<DataValidationError> validationErrors )
    {
        return new DataValidationErrors( ImmutableList.copyOf( validationErrors ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<DataValidationError> dataValidationErrors = Sets.newHashSet();

        public Builder add( final DataValidationError dataValidationError )
        {
            this.dataValidationErrors.add( dataValidationError );
            return this;
        }

        public Builder addAll( final DataValidationErrors dataValidationErrors )
        {
            this.dataValidationErrors.addAll( dataValidationErrors.getValidationErrors() );
            return this;
        }

        public DataValidationErrors build()
        {
            return new DataValidationErrors( this );
        }

    }

}
