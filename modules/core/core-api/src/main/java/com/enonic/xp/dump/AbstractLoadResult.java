package com.enonic.xp.dump;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class AbstractLoadResult
{
    private final long successful;

    private final List<LoadError> errors;

    @SuppressWarnings("unchecked")
    AbstractLoadResult( final Builder builder )
    {
        this.successful = builder.successful;
        this.errors = builder.errors;
    }

    public long getSuccessful()
    {
        return successful;
    }

    public List<LoadError> getErrors()
    {
        return errors;
    }

    public static class Builder<T, B extends Builder>
    {
        private long successful = 0L;

        private final List<LoadError> errors = new ArrayList<>();

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B successful( final long val )
        {
            successful += val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B errors( final Collection<LoadError> errors )
        {
            this.errors.addAll( errors );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B error( final LoadError error )
        {
            this.errors.add( error );
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        protected T build()
        {
            return (T) this;
        }

    }
}
