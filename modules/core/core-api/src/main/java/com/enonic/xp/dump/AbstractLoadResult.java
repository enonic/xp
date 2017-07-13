package com.enonic.xp.dump;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

abstract class AbstractLoadResult
{
    private final Long successful;

    private final List<LoadError> errors;

    @SuppressWarnings("unchecked")
    AbstractLoadResult( final Builder builder )
    {
        this.successful = builder.successful;
        this.errors = builder.errors;
    }

    public Long getSuccessful()
    {
        return successful;
    }

    public List<LoadError> getErrors()
    {
        return errors;
    }

    public static class Builder<T, B extends Builder>
    {
        private Long successful = 0L;

        private final List<LoadError> errors = Lists.newArrayList();

        protected Builder()
        {
        }

        @SuppressWarnings("unchecked")
        public B successful( final Long val )
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
